window.addEventListener('load', () => {
    const main = document.querySelector('main');

    const menuList = document.querySelectorAll('.menu');
    menuList.forEach(menu => {
        menu.addEventListener('click', (e) => {
            
            if (e.target.id === 'mute') {
                activeMute(menu);
            } else if (e.target.id === 'video') {
                activeVideo(menu);
            } else if (e.target.id === 'share-screen') {
                activeScreen(menu);
            } else if (e.target.id === 'volume') {
                volumeControl.classList.toggle('disabled');
            }

        })
    })

    const volumeControl = document.querySelector('.volume-controll');
    volumeControl.addEventListener('input', (e) => {
        if (e.target.tagName.toLowerCase() === 'input' && e.target.type ==='range') {
            changeVolume(e.target);
        }
        totalVolume();

        
    })
    

    const minShare = document.querySelector('.min-share');
    minShare.addEventListener('click', (e) => {
        if (!main.classList.contains('grid-1')) return;

        if (e.target.classList.contains('user')) {
            let share = document.querySelector('.share');
            let shareUser = document.querySelector('.share .user');
            let minShare = document.querySelector('.min-share');
        
            share.appendChild(e.target);
            minShare.appendChild(shareUser);
        }
    })

    const grids = document.querySelectorAll('input[name="grid"]');
    grids.forEach(grid => {
        grid.addEventListener('change', () => {
            initialGrid(grid);
            selectGrid(grid.id);
        })
    })

})

function totalVolume() {
    let volumeList = document.querySelectorAll('input[name="volume"]');
    let volumeAvg = 0;
    volumeList.forEach(volume => volumeAvg += Number(volume.value));
    volumeAvg /= volumeList.length;

    const svgBox = document.querySelector('#volume > .menu-svg-box');
    if (volumeAvg > 90) {
        svgBox.innerHTML = volumeHigh();
    } else if (volumeAvg > 49) {
        svgBox.innerHTML = volumeMiddle();
    } else if (volumeAvg > 0) {
        svgBox.innerHTML = volumeSmall();
    } else {
        svgBox.innerHTML = volumeMute();
    }
}
function changeVolume(target) {
    let userVolumeDiv = target.parentElement.querySelector('.user-volume');
    if (target.value > 90) {
        userVolumeDiv.innerHTML = volumeHigh();
    } else if (target.value > 49) {
        userVolumeDiv.innerHTML = volumeMiddle();
    } else if (target.value > 0) {
        userVolumeDiv.innerHTML = volumeSmall();
    } else {
        userVolumeDiv.innerHTML = volumeMute();
    }
}

function volumeHigh() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><path d="M533.6 32.5C598.5 85.2 640 165.8 640 256s-41.5 170.7-106.4 223.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C557.5 398.2 592 331.2 592 256s-34.5-142.2-88.7-186.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zM473.1 107c43.2 35.2 70.9 88.9 70.9 149s-27.7 113.8-70.9 149c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C475.3 341.3 496 301.1 496 256s-20.7-85.3-53.2-111.8c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zm-60.5 74.5C434.1 199.1 448 225.9 448 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C393.1 284.4 400 271 400 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zM301.1 34.8C312.6 40 320 51.4 320 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352H64c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3z"/></svg>`;
}
function volumeMiddle() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M333.1 34.8C344.6 40 352 51.4 352 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L163.8 352H96c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L298.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zm172 72.2c43.2 35.2 70.9 88.9 70.9 149s-27.7 113.8-70.9 149c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C507.3 341.3 528 301.1 528 256s-20.7-85.3-53.2-111.8c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5zm-60.5 74.5C466.1 199.1 480 225.9 480 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C425.1 284.4 432 271 432 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>`;
}
function volumeSmall() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352H64c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM412.6 181.5C434.1 199.1 448 225.9 448 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C393.1 284.4 400 271 400 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>`
}
function volumeMute() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352H64c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM425 167l55 55 55-55c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9l-55 55 55 55c9.4 9.4 9.4 24.6 0 33.9s-24.6 9.4-33.9 0l-55-55-55 55c-9.4 9.4-24.6 9.4-33.9 0s-9.4-24.6 0-33.9l55-55-55-55c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0z"/></svg>`;
}

function initialGrid(grid) {
    const labels = document.querySelectorAll('.grids > label');
    labels.forEach(label => label.setAttribute('aria-checked', 'false'));

    grid.parentElement.setAttribute('aria-checked', true);
}
function selectGrid(gridId) {
    const main = document.querySelector('main');
    initialMain(main);
    moveToMainShare(gridId);
    moveToMinShare(main, gridId);
    main.classList.add(gridId);
}
function moveToMainShare(gridId) {
    const share = document.querySelector('.share');
    const minShareUser = document.querySelectorAll('.min-share .user');

    const currCount = share.children.length;

    let gridCount = getGridCount(gridId);
    if (currCount < gridCount) {
        for (let i=0;i<gridCount - currCount;i++) {
            if (minShareUser[i] != null) {
                share.appendChild(minShareUser[i]);
            } else {
                return;
            }
        }
    }
}
function moveToMinShare(main, gridId) {
    const userList = document.querySelectorAll('.user');
    const minShare = document.querySelector('.min-share');

    let gridCount = getGridCount(gridId);

    if (userList.length > gridCount) {
        main.classList.add('over');
        for (let i=gridCount; i<userList.length; i++) {
            minShare.appendChild(userList[i]);
        }
    }
}
function initialMain(main) {
    main.classList.forEach(cla => {
        if (cla.indexOf('grid') != -1) {
            main.classList.remove(cla);
        }
    })
    main.classList.remove('over');
}
function getGridCount(gridId) {
    return Number(gridId.replace(/\D/g, ''));
}

function activeMute(menu) {
    let svgBox = menu.querySelector('.menu-svg-box');
    let name = menu.children.namedItem('name');
    if (isOn(menu)) {
        svgBox.innerHTML = micOff();
        name.innerHTML = '마이크 OFF';
    } else {
        svgBox.innerHTML = micOn();
        name.innerHTML = '마이크 ON';
    }
    menu.classList.toggle('on');
}
function activeVideo(menu) {
    let svgBox = menu.querySelector('.menu-svg-box');
    let name = menu.children.namedItem('name');
    if (isOn(menu)) {
        svgBox.innerHTML = videoOff();
        name.innerHTML = '웹캠 OFF';
    } else {
        svgBox.innerHTML = videoOn();
        name.innerHTML = '웹캠 ON';
    }
    menu.classList.toggle('on');

}
function activeScreen(menu) {
    if (isOn(menu)) {

    } else {
        
    }
    menu.classList.toggle('on');

}

function isOn(menu) {
    return menu.classList.contains('on');
}
function micOff() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><path d="M38.8 5.1C28.4-3.1 13.3-1.2 5.1 9.2S-1.2 34.7 9.2 42.9l592 464c10.4 8.2 25.5 6.3 33.7-4.1s6.3-25.5-4.1-33.7L472.1 344.7c15.2-26 23.9-56.3 23.9-88.7V216c0-13.3-10.7-24-24-24s-24 10.7-24 24v40c0 21.2-5.1 41.1-14.2 58.7L416 300.8V96c0-53-43-96-96-96s-96 43-96 96v54.3L38.8 5.1zm362.5 407l-43.1-33.9C346.1 382 333.3 384 320 384c-70.7 0-128-57.3-128-128v-8.7L144.7 210c-.5 1.9-.7 3.9-.7 6v40c0 89.1 66.2 162.7 152 174.4V464H248c-13.3 0-24 10.7-24 24s10.7 24 24 24h72 72c13.3 0 24-10.7 24-24s-10.7-24-24-24H344V430.4c20.4-2.8 39.7-9.1 57.3-18.2z"/></svg>`;
}
function micOn() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M192 0C139 0 96 43 96 96V256c0 53 43 96 96 96s96-43 96-96V96c0-53-43-96-96-96zM64 216c0-13.3-10.7-24-24-24s-24 10.7-24 24v40c0 89.1 66.2 162.7 152 174.4V464H120c-13.3 0-24 10.7-24 24s10.7 24 24 24h72 72c13.3 0 24-10.7 24-24s-10.7-24-24-24H216V430.4c85.8-11.7 152-85.3 152-174.4V216c0-13.3-10.7-24-24-24s-24 10.7-24 24v40c0 70.7-57.3 128-128 128s-128-57.3-128-128V216z"/></svg>`
}
function videoOn() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M0 128C0 92.7 28.7 64 64 64H320c35.3 0 64 28.7 64 64V384c0 35.3-28.7 64-64 64H64c-35.3 0-64-28.7-64-64V128zM559.1 99.8c10.4 5.6 16.9 16.4 16.9 28.2V384c0 11.8-6.5 22.6-16.9 28.2s-23 5-32.9-1.6l-96-64L416 337.1V320 192 174.9l14.2-9.5 96-64c9.8-6.5 22.4-7.2 32.9-1.6z"/></svg>`;
}
function videoOff() {
    return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><path d="M38.8 5.1C28.4-3.1 13.3-1.2 5.1 9.2S-1.2 34.7 9.2 42.9l592 464c10.4 8.2 25.5 6.3 33.7-4.1s6.3-25.5-4.1-33.7l-86.4-67.7 13.8 9.2c9.8 6.5 22.4 7.2 32.9 1.6s16.9-16.4 16.9-28.2V128c0-11.8-6.5-22.6-16.9-28.2s-23-5-32.9 1.6l-96 64L448 174.9V192 320v5.8l-32-25.1V128c0-35.3-28.7-64-64-64H113.9L38.8 5.1zM407 416.7L32.3 121.5c-.2 2.1-.3 4.3-.3 6.5V384c0 35.3 28.7 64 64 64H352c23.4 0 43.9-12.6 55-31.3z"/></svg>`;
}