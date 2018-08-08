export function formatDate(dateString) {
    let date = new Date(Number(dateString));
    let year = date.getFullYear();
    let month = '' + (date.getMonth() + 1);
    let day = '' + date.getDate();
    let hours = '' + date.getHours();
    let minutes = '' + date.getMinutes();
    let seconds = '' + date.getSeconds();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    if (hours.length < 2) hours = '0' + hours;
    if (minutes.length < 2) minutes = '0' + minutes;
    if (seconds.length < 2) seconds = '0' + seconds;

    return [year, month, day].join('-') + " " + [hours, minutes, seconds].join(':');
}

export function isEmailValid(email) {
    return /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test(email);
}

export function getPercentageValue(part, whole) {
    if (whole === 0) {
        return '';
    } else {
        return ' (' + (part / whole * 100).toFixed(2) + ')%';
    }
}