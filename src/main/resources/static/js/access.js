import {isEmailValid} from './functions.js';
import {msgMap} from './messagesMap.js';

$(function() {
    $('.tab_link_button').click(showTab);
    $('#tab_log_in_button').click();

    $("#login_form").submit(login);
    $("#register_form").submit(register);
});

function showTab() {
    let buttonId = this.id;

    // hide all tab_content (display: none)
    $('.tab_content').hide();

    // remove class 'active' from all tab_link_button
    $('.tab_link_button').removeClass('active');

    // show pointed tab (display: block)
    $('#' + buttonId.replace('_button', '')).show();

    // set focus on first visible input
    $('input:visible:first').focus();

    // add 'active' class for pointed button
    $('#' + buttonId).addClass('active');

    // clear entered data
    $('.tab_content form').trigger('reset');
    $('.tab_content form div').empty();
}

function login(event) {
    event.preventDefault();

    let username = $('#login_form_username');
    let password = $('#login_form_password');
    let errmsg = $('#login_form_errmsg');

    if (username.val().trim() !== '' && password.val().trim() !== '') {
        let request = new XMLHttpRequest();
        let url = '/app/session';
        let params = JSON.stringify({
            'username': username.val(),
            'password': password.val()
        });

        request.open("POST", url, true);
        request.setRequestHeader("Content-type", "application/json; charset=utf-8");
        request.onload = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                window.location.href="home.html";
            } else {
                errmsg.text('Bad credentials.');
            }
        };
        request.send(params);
    } else {
        errmsg.text('Please enter your credentials.');
    }
}

function register(event) {
    event.preventDefault();

    let username = $('#register_form_username');
    let email = $('#register_form_email');
    let password = $('#register_form_password');
    let errmsg = $('#register_form_errmsg');

    console.log('username: ' + username.val());
    console.log('email: ' + email.val());
    console.log('password: ' + password.val());

    if (username.val().trim() !== '' && email.val().trim() !== '' && password.val().trim() !== '') {
        if (isEmailValid(email.val())) {
            let request = new XMLHttpRequest();
            let url = '/app/register';
            let params = JSON.stringify({
                'username': username.val(),
                'email': email.val(),
                'password': password.val()
            });

            request.open("POST", url, true);
            request.setRequestHeader("Content-type", "application/json; charset=utf-8");
            request.onload = function () {
                if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                    $('#tab_log_in_button').click();
                    $('#login_form_msg').text('Your account has been successfully created.');
                } else {
                    errmsg.text(msgMap[JSON.parse(this.responseText).messageKey]);
                }
            };
            request.send(params);
        } else {
            errmsg.text('Please enter valid email address.');
        }
    } else {
        errmsg.text('Please fill out all required fields.');
    }
}
