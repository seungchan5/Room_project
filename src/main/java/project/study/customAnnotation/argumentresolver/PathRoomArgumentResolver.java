package project.study.customAnnotation.argumentresolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;
import project.study.customAnnotation.PathRoom;
import project.study.domain.Room;
import project.study.jpaRepository.RoomJpaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class PathRoomArgumentResolver extends AbstractPathRoomMethodArgumentResolver
        implements UriComponentsContributor {

    private final RoomJpaRepository roomJpaRepository;
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);


    @Override
    protected Optional<Room> findByRoomId(Long roomId) {
        return roomJpaRepository.findById(roomId);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(PathRoom.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            PathRoom pathRoom = parameter.getParameterAnnotation(PathRoom.class);

            return (pathRoom != null && StringUtils.hasText(pathRoom.value()));
        }
        return true;
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        PathRoom ann = parameter.getParameterAnnotation(PathRoom.class);
        Assert.state(ann != null, "No PathVariable annotation");
        return new PathVariableNamedValueInfo(ann);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingPathVariableException(name, parameter);
    }

    @Override
    protected void handleMissingValueAfterConversion(
            String name, MethodParameter parameter, NativeWebRequest request) throws Exception {

        throw new MissingPathVariableException(name, parameter, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter,
                                       @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request) {

        String key = View.PATH_VARIABLES;
        int scope = RequestAttributes.SCOPE_REQUEST;
        Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(key, scope);
        if (pathVars == null) {
            pathVars = new HashMap<>();
            request.setAttribute(key, pathVars, scope);
        }
        pathVars.put(name, arg);
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value,
                                         UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {

        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            return;
        }

        PathRoom ann = parameter.getParameterAnnotation(PathRoom.class);
        String name = (ann != null && StringUtils.hasLength(ann.value()) ? ann.value() : parameter.getParameterName());
        String formatted = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
        uriVariables.put(name, formatted);
    }

    @Nullable
    protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, Object value) {
        if (value instanceof String string) {
            return string;
        }
        else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        else {
            return value.toString();
        }
    }


    private static class PathVariableNamedValueInfo extends NamedValueInfo {

        public PathVariableNamedValueInfo(PathRoom annotation) {
            super(annotation.name(), annotation.required(), ValueConstants.DEFAULT_NONE);
        }
    }

}
