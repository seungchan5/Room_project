package project.study.customAnnotation.argumentresolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import project.study.customAnnotation.PathRoom;
import project.study.domain.Room;
import project.study.exceptions.roomjoin.IllegalRoomException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPathRoomMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Nullable
    private final ConfigurableBeanFactory configurableBeanFactory;

    @Nullable
    private final BeanExpressionContext expressionContext;

    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache = new ConcurrentHashMap<>(256);


    public AbstractPathRoomMethodArgumentResolver() {
        this.configurableBeanFactory = null;
        this.expressionContext = null;
    }

    public AbstractPathRoomMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
        this.expressionContext =
                (beanFactory != null ? new BeanExpressionContext(beanFactory, new RequestScope()) : null);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        System.out.println("시작");
        boolean hasPathRoomAnnotation = parameter.hasParameterAnnotation(PathRoom.class);
        boolean hasRoomType = Room.class.isAssignableFrom(parameter.getParameterType());
        return hasPathRoomAnnotation && hasRoomType;
    }

    @Override
    @Nullable
    public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Object resolvedName = resolveEmbeddedValuesAndExpressions(namedValueInfo.name);

        if (resolvedName == null) throw new IllegalArgumentException("Specified name must not resolve to null: [" + namedValueInfo.name + "]");

        Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);
        Long roomId = getRoomId(arg, webRequest);

        Optional<Room> findRoom = findByRoomId(roomId);
        if (findRoom.isEmpty()) throw new IllegalRoomException((HttpServletResponse) webRequest.getNativeResponse(), "방 정보를 찾을 수 없습니다.");

        Room room = findRoom.get();
        if (room.isDeleteRoom()) throw new IllegalRoomException((HttpServletResponse) webRequest.getNativeResponse(), "이미 삭제된 방입니다.");

        return findRoom.get();
    }

    protected abstract Optional<Room> findByRoomId(Long roomId);

    private Long getRoomId(Object arg, NativeWebRequest webRequest) {
        try {
            return Long.parseLong((String) arg);
        } catch (NumberFormatException e) {
            HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
            throw new IllegalRoomException(response, "존재하지 않는 방입니다.");
        }
    }


    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
        if (namedValueInfo == null) {
            namedValueInfo = createNamedValueInfo(parameter);
            namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo);
            this.namedValueInfoCache.put(parameter, namedValueInfo);
        }
        return namedValueInfo;
    }

    protected abstract NamedValueInfo createNamedValueInfo(MethodParameter parameter);

    /**
     * Create a new NamedValueInfo based on the given NamedValueInfo with sanitized values.
     */
    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException("""
						Name for argument of type [%s] not specified, and parameter name information not \
						available via reflection. Ensure that the compiler uses the '-parameters' flag."""
                        .formatted(parameter.getNestedParameterType().getName()));
            }
        }
        String defaultValue = (ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue);
        return new NamedValueInfo(name, info.required, defaultValue);
    }

    /**
     * Resolve the given annotation-specified value,
     * potentially containing placeholders and expressions.
     */
    @Nullable
    private Object resolveEmbeddedValuesAndExpressions(String value) {
        if (this.configurableBeanFactory == null || this.expressionContext == null) {
            return value;
        }
        String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null) {
            return value;
        }
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
    }

    @Nullable
    protected abstract Object resolveName(String name, MethodParameter parameter, NativeWebRequest request)
            throws Exception;


    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request)
            throws Exception {

        handleMissingValue(name, parameter);
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new ServletRequestBindingException("Missing argument '" + name +
                "' for method parameter of type " + parameter.getNestedParameterType().getSimpleName());
    }

    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request)
            throws Exception {

        handleMissingValue(name, parameter, request);
    }

    @Nullable
    private Object handleNullValue(String name, @Nullable Object value, Class<?> paramType) {
        if (value == null) {
            if (Boolean.TYPE.equals(paramType)) {
                return Boolean.FALSE;
            }
            else if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType.getSimpleName() + " parameter '" + name +
                        "' is present but cannot be translated into a null value due to being declared as a " +
                        "primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }

    @Nullable
    private static Object convertIfNecessary(
            MethodParameter parameter, NativeWebRequest webRequest, WebDataBinderFactory binderFactory,
            NamedValueInfo namedValueInfo, @Nullable Object arg) throws Exception {

        WebDataBinder binder = binderFactory.createBinder(webRequest, null, namedValueInfo.name);
        try {
            arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
        }
        catch (ConversionNotSupportedException ex) {
            throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(),
                    namedValueInfo.name, parameter, ex.getCause());
        }
        catch (TypeMismatchException ex) {
            throw new MethodArgumentTypeMismatchException(arg, ex.getRequiredType(),
                    namedValueInfo.name, parameter, ex.getCause());
        }
        return arg;
    }

    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter,
                                       @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
    }

    protected static class NamedValueInfo {

        private final String name;

        private final boolean required;

        @Nullable
        private final String defaultValue;

        public NamedValueInfo(String name, boolean required, @Nullable String defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }

    private static class KotlinDelegate {

        public static boolean hasDefaultValue(MethodParameter parameter) {
            Method method = Objects.requireNonNull(parameter.getMethod());
            KFunction<?> function = kotlin.reflect.jvm.ReflectJvmMapping.getKotlinFunction(method);
            if (function != null) {
                int index = 0;
                for (KParameter kParameter : function.getParameters()) {
                    if (KParameter.Kind.VALUE.equals(kParameter.getKind()) && parameter.getParameterIndex() == index++) {
                        return kParameter.isOptional();
                    }
                }
            }
            return false;
        }
    }
}
