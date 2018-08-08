import {formatDate, getPercentageValue} from './functions.js';
import {msgMap} from './messagesMap.js';

let currentUser = null;
let stompClient = null;
let currentConversationId = null;
let subscriptions = {};

$(function() {
    connectToServer();
    getCurrentUser();
    getConversations();

    $('#logout_button').click(logout);
    $('#tab_default_button').click(openDefaultTab);
    $('#tab_open_conversation_button').click(openNewConversationTab);
    openNewConversationTab();

    $('#conversations_list')
        .on('click', 'div p', showStatsOfConversation)
        .on('click', 'div i', activateConversation);
    $('#search_form').submit(search);
    $('#new_conv_form').submit(addNewConversation);

    $('#open_conversations_list')
        .on('click', 'div', showConversation);

    let delay = 1000, setTimeoutConst;
    $('#chat_message_list').on({
        mouseenter: function (event) {
            setTimeoutConst = setTimeout(function(){
                $(event.target).closest('.chat_message').find(':first-child').css({display: 'flex'});
            }, delay);
        },
        mouseleave: function () {
            clearTimeout(setTimeoutConst);
            $(this).closest('.chat_message').find(':first-child').css({display: 'none'});
        }
    }, '.chat_message');
    $('#send_message_form_message').keypress(function(event) {
        if (event.keyCode === 13 && !event.shiftKey) {
            event.preventDefault();
            $('#send_message_form_submit').focus();
            // $('#send_message_form_submit').submit();
        } else {
            $(this).height(0);
            $(this).height(this.scrollHeight);
        }
    });
    $('#send_message_form').submit(sendMessage);
    $('#new_game_form').submit(createNewGame);

    $(window).resize(setBoardDimension);
});

function logout() {
    let request = new XMLHttpRequest();
    let url = '/app/session';

    request.open('DELETE', url, true);
    request.onload = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            window.location.href='/';
        } else {
            console.log('ERROR: logout()');
            alert('Logout Error');
        }
    };
    request.send(null);
}

function getCurrentUser() {
    let request = new XMLHttpRequest();
    let url = '/app/session';

    request.open('GET', url, true);
    request.onload = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            currentUser = JSON.parse(this.responseText).username;
            $('h2.username').text(currentUser);
        } else {
            console.log('ERROR: getCurrentUser()');
            alert('getCurrentUser error');
        }
    };
    request.send(null);
}

function openDefaultTab() {
    $('#stats_area').hide();
    $('.tab_link_button').removeClass('active');
    $('#tab_default_button').addClass('active');

    $('.tab-default').show();
    $('.tab-open-new').hide();

    $('div.msg').empty();
}

function openNewConversationTab() {
    $('#open_conversations_list div#' + currentConversationId).removeClass('active');
    currentConversationId = null;
    $('#chat_message_list').empty();

    $('.tab_link_button').removeClass('active');
    $('#tab_open_conversation_button').addClass('active');

    $('.tab-default').hide();
    $('.tab-open-new').show();

    $('div.msg').empty();
}


// -------------------------- tab-open-new --------------------------
function getConversations() {
    let request = new XMLHttpRequest();
    let url = '/app/participations';

    request.open('GET', url, true);
    request.onload = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let usersConversations = $(JSON.parse(this.responseText));

            let conversationsList = $('#conversations_list');

            usersConversations.each(function() {
                let listElement = $(document.createElement('div')).attr('id', this.conversationId);
                let elementName = $(document.createElement('p')).text(this.conversationName);
                let elementIcon = $(document.createElement('i')).addClass('fas fa-envelope-open');

                listElement.append([elementName, elementIcon]);
                conversationsList.append(listElement);
            });

        } else {
            console.log('ERROR: getConversations()');
            alert('getConversations error');
        }
    };
    request.send(null);
}

function showStatsOfConversation() {
    let conversationId = $(this).parent().attr('id');
    let conversationName = $(this).parent().find(':first-child').text();

    let request = new XMLHttpRequest();
    let url = '/app/stats/' + conversationId;

    request.open('GET', url, true);
    request.onload = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let stats = JSON.parse(this.responseText);
            let messageStats = $(stats.messageStats);
            let archiveStats = $(stats.archiveStats);

            let messageCount = 0;
            let gamesCount = 0;
            let drawsCount = 0;

            let user1 = {
                username: currentUser,
                messageCount: 0, firstMessage: '-', lastMessage: '-',
                gamesWon: 0, asWhite: 0, asBlack: 0
            };

            let user2 = {
                username: conversationName,
                messageCount: 0, firstMessage: '-', lastMessage: '-',
                gamesWon: 0, asWhite: 0, asBlack: 0
            };

            messageStats.each(function() {
                if (this.author === currentUser) {
                    user1.messageCount = this.count;
                    user1.firstMessage = formatDate(this.first);
                    user1.lastMessage = formatDate(this.last);
                } else {
                    user2.username = this.author;
                    user2.messageCount = this.count;
                    user2.firstMessage = formatDate(this.first);
                    user2.lastMessage = formatDate(this.last);
                }
            });

            archiveStats.each(function() {
                switch (this.result) {
                    case 0:
                        if (this.white === currentUser) {
                            user2.asBlack = this.count;
                        } else {
                            user1.asBlack = this.count;
                        }
                        break;
                    case 1:
                        if (this.white === currentUser) {
                            user1.asWhite = this.count;
                        } else {
                            user2.asWhite = this.count;
                        }
                        break;
                    case 2:
                        drawsCount += this.count;
                }
            });

            messageCount = user1.messageCount + user2.messageCount;
            user1.gamesWon = user1.asWhite + user1.asBlack;
            user2.gamesWon = user2.asWhite + user2.asBlack;
            gamesCount = user1.gamesWon + user2.gamesWon + drawsCount;

            $('#messages_count').text(messageCount);
            $('#games_count').text(gamesCount);
            $('#games_draws').text(drawsCount);

            $('#user1').text(user1.username);
            $('#user1_msg_send').text(user1.messageCount + getPercentageValue(user1.messageCount, messageCount));
            $('#user1_first_message').text(user1.firstMessage);
            $('#user1_last_message').text(user1.lastMessage);
            $('#user1_games_won').text(user1.gamesWon + getPercentageValue(user1.gamesWon, gamesCount));
            $('#user1_as_white').text(user1.asWhite + getPercentageValue(user1.asWhite, user1.gamesWon));
            $('#user1_as_black').text(user1.asBlack + getPercentageValue(user1.asBlack, user1.gamesWon));

            $('#user2').text(user2.username);
            $('#user2_msg_send').text(user2.messageCount + getPercentageValue(user2.messageCount, messageCount));
            $('#user2_first_message').text(user2.firstMessage);
            $('#user2_last_message').text(user2.lastMessage);
            $('#user2_games_won').text(user2.gamesWon + getPercentageValue(user2.gamesWon, gamesCount));
            $('#user2_as_white').text(user2.asWhite + getPercentageValue(user2.asWhite, user2.gamesWon));
            $('#user2_as_black').text(user2.asBlack + getPercentageValue(user2.asBlack, user2.gamesWon));

            $('#stats_area').show();
        } else {
            console.log('ERROR: showStatsOfConversation()');
            alert('showStatsOfConversation error');
        }
    };
    request.send(null);
}

function activateConversation() {
    let conversationId = $(this).parent().attr('id');
    let conversationName = $(this).parent().find(':first-child').text();
    console.log('activate conversation id: ' + conversationId + ', name: ' + conversationName);

    let listElement = $(document.createElement('div')).attr('id', conversationId);
    let msgNotification = $(document.createElement('i')).addClass('fas fa-envelope').hide();
    let gameNotification = $(document.createElement('i')).addClass('fas fa-chess-pawn').hide();
    let elementName = $(document.createElement('p')).text(conversationName);

    listElement.append([msgNotification, gameNotification, elementName]);
    $('#open_conversations_list').append(listElement);

    subscribe(conversationId);

    $('#conversations_list div#' + conversationId).addClass('already_open');
}

function search(event) {
    event.preventDefault();

    $('#conversations_list div').hide();

    let phrase = $('#search_form_phrase').val().replace(/\s+/g, '').toLowerCase();

    $('#conversations_list div p').each(function() {
        if ($(this).text().replace(/\s+/g, '').toLowerCase().indexOf(phrase) >= 0) {
            $(this).parent().show();
        }
    });
}

function addNewConversation(event) {
    event.preventDefault();

    let username = $('#new_conv_form_username');
    let errmsg = $('#new_conv_form_errmsg');

    if (username.val().trim() !== '') {
        let request = new XMLHttpRequest();
        let url = '/app/conversation';
        let params = JSON.stringify({
            'invitationSender': currentUser,
            'invitationReceiver': username.val()
        });

        request.open('POST', url, true);
        request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
        request.onload = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                $('#new_conv_form').trigger('reset');
                $('#new_conv_form div').empty();

                $('#conversations_list').empty();
                getConversations();

                let msg = $('#new_conv_form_msg');
                msg.text('Conversation added successfully.');
                setTimeout(function() {
                    msg.empty();
                }, 5000);
            } else {
                errmsg.text(msgMap[JSON.parse(this.responseText).messageKey]);
            }
        };
        request.send(params);
    } else {
        errmsg.text('Please enter username.');
    }
}


// -------------------------- tab-default --------------------------
function showConversation() {
    let conversationId = $(this).attr('id');
    currentConversationId = conversationId;
    console.log('open conversation: ' + conversationId);

    $('#chat_message_list').empty();

    // data from server
    let request = new XMLHttpRequest();
    let url = '/app/conversation/' + conversationId;

    request.open('GET', url, true);
    request.onload = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            let conversation = JSON.parse(this.responseText);
            let messages = $(conversation.messages);
            let gameObj = conversation.game;

            messages.each(function() {
                printMessage(this.author, this.content, this.sendingDate, false);
            });
            let chatMessageList = $('#chat_message_list');
            chatMessageList.scrollTop(chatMessageList.prop('scrollHeight'));

            currentTurn.loadGame(gameObj);
            setBoardDimension();
        } else {
            console.log('ERROR: showConversation()');
            alert('showConversation error');
        }
    };
    request.send(null);

    $('#open_conversations_list div').removeClass('active');
    $(this).addClass('active');
    $(this).find('i').hide();
    openDefaultTab();
}

function printMessage(author, content, sendingDate, scrollTop) {
    let chatMessage = $(document.createElement('div')).addClass('chat_message');
    let chatMessageDetails = $(document.createElement('div')).addClass('chat_message_details');

    let chatMessageAuthor = $(document.createElement('div')).addClass('chat_message_author').text(author);
    let chatMessageDate = $(document.createElement('div')).addClass('chat_message_date').text(formatDate(sendingDate));
    let chatMessageContent = $(document.createElement('pre')).addClass('chat_message_content').text(content);

    if (author === currentUser) {
        chatMessage.addClass('my_message');
    }

    chatMessageDetails.append([chatMessageAuthor, chatMessageDate]);
    chatMessage.append([chatMessageDetails, chatMessageContent]);

    let chatMessageList = $('#chat_message_list');
    chatMessageList.append(chatMessage);

    // scrollTop takes a lot of time
    if (scrollTop) {
        chatMessageList.scrollTop(chatMessageList.prop('scrollHeight'));
    }
}

// ---------- game ----------
function setBoardDimension() {
    let gameBoard = $('#game_board');
    gameBoard.height(gameBoard.width());
}

function showGameBoard() {
    $('.game_section div.tab-default.container div').show();
    $('.game_section footer').hide();
}

function showNewGameForm() {
    let conversationCompanion = $('#open_conversations_list div.active p').text();

    $('#new_game_form_white_opt_1')
        .text(currentUser)
        .val(currentUser + ';' + conversationCompanion);
    $('#new_game_form_white_opt_2')
        .text(conversationCompanion)
        .val(conversationCompanion + ';' + currentUser);

    $('.game_section div.tab-default.container div').hide();
    $('.game_section footer').show();
}

function createNewGame(event) {
    event.preventDefault();

    let colorOrder = $('#new_game_form_white option:selected');
    let errmsg = $('#new_game_form_errmsg');

    if (colorOrder.val().trim() !== '') {
        let players = colorOrder.val().split(';', 2);

        let request = new XMLHttpRequest();
        let url = '/app/newGame';
        let params = JSON.stringify({
            'idConversation': currentConversationId,
            'white': players[0],
            'black': players[1]
        });

        request.open('POST', url, true);
        request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
        request.onload = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                $('#new_game_form').trigger('reset');
                $('#new_game_form div').empty();

                let msg = $('#new_game_form_msg');
                msg.text('Game created successfully.');
                setTimeout(function() {
                    msg.empty();
                }, 5000);
            } else {
                errmsg.text(msgMap[JSON.parse(this.responseText).messageKey]);
            }
        };
        request.send(params);
    } else {
        errmsg.text('Please select player.');
        setTimeout(function() {
            errmsg.empty();
        }, 5000);
    }
}

let currentTurn = {
    gameId: null,
    initialBoardState: null,
    userTurn: null,
    userColor: null,
    userPieceSelector: null,
    opponentPieceSelector: null,
    fields: null,
    couldEliminate: null,

    setUserColor: function(userColor) {
        this.userColor = userColor;
        if (this.userColor === 1) {
            this.userPieceSelector = '.white_piece, .white_crowned_piece';
            this.opponentPieceSelector = '.black_piece, .black_crowned_piece';
        } else {
            this.userPieceSelector = '.black_piece, .black_crowned_piece';
            this.opponentPieceSelector = '.white_piece, .white_crowned_piece';
        }
    },

    resetGameArea: function() {
        this.fields.off().removeAttr('style').removeClass('user_turn').removeClass('opponent_turn').removeClass('possible_move');
        this.fields.find('div').off().removeAttr('style').removeClass().hide();
        clearCurrentlySelectedPiece();
    },
    loadGame: function(gameObj) {
        this.fields = $('.field');
        if (gameObj !== null) {
            this.gameId = gameObj.idGame;
            this.setUserColor((gameObj.white === currentUser) ? 1 : 0);
            this.initGame(gameObj.boardState, (gameObj.currentTurn === this.userColor));
            this.info();
            showGameBoard();
        } else {
            showNewGameForm();
        }
    },
    updateGame: function(gameTurnDTO) {
        if (gameTurnDTO.gameOver !== null) {
            alert('game over: ' + gameTurnDTO.gameOver);
            if (gameTurnDTO.gameOver === 0) {
                //black win
                $('#game_msg').text('Black win!');
            } else {
                //white win
                $('#game_msg').text('White win!');
            }
            // showNewGameForm();
        } else {
            this.initGame(gameTurnDTO.boardState, (gameTurnDTO.turn === this.userColor));
        }
    },
    initGame: function(boardState, userTurn) {
        this.initialBoardState = boardState;
        this.userTurn = userTurn;

        // reset board
        this.resetGameArea();

        // print game board
        this.fields.each(function(i) {
            switch (boardState.charAt(i)) {
                case 'w':
                    $(this).find('div').addClass('white_piece').show();
                    break;
                case 'W':
                    $(this).find('div').addClass('white_crowned_piece').show();
                    break;
                case 'b':
                    $(this).find('div').addClass('black_piece').show();
                    break;
                case 'B':
                    $(this).find('div').addClass('black_crowned_piece').show();
            }
        });

        // check if could eliminate in this turn
        this.couldEliminate = this.checkIfCouldEliminate();

        // add click event handler if isUserTurn
        if (userTurn) {
            this.fields.on('click', this.userPieceSelector, selectPiece);
            $(this.userPieceSelector).css('cursor', 'pointer').parent().addClass('user_turn');
        } else {
            $(this.opponentPieceSelector).parent().addClass('opponent_turn');
        }
    },
    checkIfCouldEliminate: function() {
        let flag = false;
        $(currentTurn.userPieceSelector).each(function() {
            let userPiece = new SelectedPiece(this.className, $(this).parent().data('row'), $(this).parent().data('col'));
            userPiece.findMoves();

            if (userPiece.couldEliminate) {
                flag = true;
                return false;
            }
        });
        return flag;
    },
    boardStateToString: function() {
        let boardStateStr = '';
        $('.field div').each(function() {
            let row = $(this).parent().data('row');
            if ($(this).hasClass('white_piece')) {
                if (row === 1) {
                    boardStateStr += 'W';
                } else {
                    boardStateStr += 'w';
                }
            } else if ($(this).hasClass('black_piece')) {
                if (row === 8) {
                    boardStateStr += 'B';
                } else {
                    boardStateStr += 'b';
                }
            } else if ($(this).hasClass('white_crowned_piece')) {
                boardStateStr += 'W';
            } else if ($(this).hasClass('black_crowned_piece')) {
                boardStateStr += 'B';
            } else {
                boardStateStr += '-';
            }
        });
        return boardStateStr;
    },
    finalizeMove: function() {
        // block further actions
        $('.field div').off().removeClass('selected').removeAttr('style').show();
        $('.field').off().removeClass('user_turn');

        endTurn(
            this.gameId,
            Math.abs(this.userColor - 1),
            ($(this.opponentPieceSelector).length === 0) ? this.userColor : null,
            this.boardStateToString());
    },

    info: function() {
        console.log('CurrentTurn:');
        console.log('    gameId: ' + this.gameId);
        console.log('    userTurn: ' + this.userTurn);
        console.log('    userColor: ' + this.userColor);
        console.log('    userPieceSelector: ' + this.userPieceSelector);
        console.log('    opponentPieceSelector: ' + this.opponentPieceSelector);
        console.log('    couldEliminate: ' + this.couldEliminate);
    }
};

function SelectedPiece(type, row, col) {
    this.type = type;
    this.row = row;
    this.col = col;
    this.moveSet = [];
    this.couldEliminate = false;

    this.toString = function() {
        console.log('SelectedPiece: ['+this.row+','+this.col+'] <'+this.type+'> <'+this.couldEliminate+'> moveSet: ' + this.moveSet.length);
        $(this.moveSet).each(function() {
            console.log(this);
        });
    };

    this.checkDirection = function(opponentColor, rowStep, colStep) {
        let r = this.row + rowStep;
        let c = this.col + colStep;
        // console.log('checkDirection ['+r+','+c+']');

        if (r > 0 && r < 9 && c > 0 && c < 9) {
            let pieceType = $('#row-' + r + '_col-' + c).find('div').attr('class');
            if (pieceType.includes(opponentColor)) {
                let nextR = r + rowStep;
                let nextC = c + colStep;
                if ($('#row-' + nextR + '_col-' + nextC).find('div').attr('class') === '') {
                    return {newRow: nextR, newCol: nextC, eliminated: {row: r, col: c}};
                }
            }
            if (pieceType === '') {
                return {newRow: r, newCol: c, eliminated: null};
            }
        }
        return null;
    };
    this.addMove = function addMove(move) {
        if (move !== null) {
            this.moveSet.push(move);
        }
    };
    this.findMoves = function() {
        switch (this.type) {
            case 'white_piece':
                this.addMove(this.checkDirection('black', -1, -1));
                this.addMove(this.checkDirection('black', -1, 1));
                break;
            case 'white_crowned_piece':
                this.addMove(this.checkDirection('black', -1, -1));
                this.addMove(this.checkDirection('black', -1, 1));
                this.addMove(this.checkDirection('black', 1, -1));
                this.addMove(this.checkDirection('black', 1, 1));
                break;
            case 'black_piece':
                this.addMove(this.checkDirection('white', 1, -1));
                this.addMove(this.checkDirection('white', 1, 1));
                break;
            case 'black_crowned_piece':
                this.addMove(this.checkDirection('white', -1, -1));
                this.addMove(this.checkDirection('white', -1, 1));
                this.addMove(this.checkDirection('white', 1, -1));
                this.addMove(this.checkDirection('white', 1, 1));
                break;
            default:
                this.moveSet = null;
        }

        // sieve moves: if couldEliminate hide all plain moves
        let plainMoves = [];

        $(this.moveSet).each(function(i) {
            if (this.eliminated === null) {
                plainMoves.push(i);
            }
        });

        let moveSet = this.moveSet;
        if (moveSet.length !== plainMoves.length) {
            this.couldEliminate = true;
            $(plainMoves.reverse()).each(function () {
                moveSet.splice(this, 1);
            });
            // console.log('removed: ' + plainMoves.length + ' elements');
        }
    };
    this.highlightMoves = function() {
        if ( (!currentTurn.couldEliminate) || (currentTurn.couldEliminate && this.couldEliminate) ) {
            $(this.moveSet).each(function() {
                $('#row-' + this.newRow + '_col-' + this.newCol).addClass('possible_move').data('info', this);
            });
            $('.possible_move').css('cursor', 'pointer').on('click', makeMove);
        } else {
            let errmsg = $('#game_errmsg');
            errmsg.text('Capturing is mandatory!');
            setTimeout(function() {
                errmsg.empty();
            }, 3000);
        }
    };
}

let currentlySelectedPiece = null;

function selectPiece() {
    let r = $(this).parent().data('row');
    let c = $(this).parent().data('col');

    if (currentlySelectedPiece !== null) {
        if (currentlySelectedPiece.row === r && currentlySelectedPiece.col === c) {
            // piece was selected twice in a row
            clearCurrentlySelectedPiece();
            return false;
        }
    }
    clearCurrentlySelectedPiece();

    currentlySelectedPiece = new SelectedPiece(this.className, r, c);
    currentlySelectedPiece.findMoves();
    currentlySelectedPiece.highlightMoves();
    currentlySelectedPiece.toString();

    $(this).addClass('selected');
}

function clearCurrentlySelectedPiece() {
    $('.possible_move').removeClass('possible_move').css('cursor', 'default');
    $('.field div').removeClass('selected');
    currentlySelectedPiece = null;
}

function makeMove() {
    let info = $(this).data('info');
    let newField = $('#row-' + info.newRow + '_col-' + info.newCol);
    let selectedPieceObj = $('.field .selected').removeClass('selected');
    let selectedPieceType = selectedPieceObj.attr('class');

    // remove selected piece from old field
    selectedPieceObj.removeClass().hide();

    // remove new field highlight
    $('.possible_move').off().removeClass('possible_move').removeAttr('style');

    // show piece on new field
    newField.find('div').addClass(selectedPieceType + ' selected').show();

    if (info.eliminated) {
        // eliminate opponent piece
        console.log('eliminate: ['+info.eliminated.row+','+info.eliminated.col+']');
        $('#row-' + info.eliminated.row + '_col-' + info.eliminated.col).find('div').removeClass();

        // clearCurrentlySelectedPiece();
        currentlySelectedPiece = new SelectedPiece(selectedPieceType, info.newRow, info.newCol);
        currentlySelectedPiece.findMoves();

        if (currentlySelectedPiece.couldEliminate) {
            // force user to finish combo
            $('.field').off('click');
            $('.field div').css('cursor', 'default');
            currentlySelectedPiece.highlightMoves();
        } else {
            currentTurn.finalizeMove();
        }
    } else {
        currentTurn.finalizeMove();
    }
}

// -------------------------- chat service --------------------------
function connectToServer() {
    let socket = new SockJS('/activechat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connect_callback, error_callback);
}

function connect_callback(frame) {
    console.log('Connected to server');
}

function error_callback(error) {
    console.log('Could not connect to WebSocket server. Please refresh this page to try again!');
}

function subscribe(conversationId) {
    if (stompClient != null) {
        subscriptions[conversationId] = stompClient.subscribe('/room/' + conversationId, callback, {id: conversationId});

        // console.log('Subscriptions: [' + Object.getOwnPropertyNames(stompClient.subscriptions) + ']');
        console.log('Subscriptions: [' + Object.getOwnPropertyNames(subscriptions) + ']');
    }
}

function unsubscribe(conversationId) {
    subscriptions[conversationId].unsubscribe();
    delete subscriptions[conversationId];
}

function callback(message) {
    console.log('subscription [' + message.headers.subscription + '] receive message; active subscription: [' + currentConversationId + ']');

    if (message.body) {
        let messageObj = JSON.parse(message.body);
        let isChatMessage = messageObj.hasOwnProperty('content');

        // console.log('isChatMessage: ' + isChatMessage);

        if (currentConversationId === message.headers.subscription) {
            if (isChatMessage) {
                // show chat message
                printMessage(
                    messageObj.author,
                    messageObj.content,
                    messageObj.sendingDate,
                    true
                );
            } else {
                // refresh game board
                currentTurn.updateGame(messageObj);
            }
        } else {
            // show alert
            let notificationClass;
            isChatMessage ? notificationClass = 'fa-envelope' : notificationClass = 'fa-chess-pawn';

            console.log('add alert: [' + notificationClass + '], conversationId: ' + message.headers.subscription);

            $('#open_conversations_list div#' + message.headers.subscription).find('i.' + notificationClass).show();
        }
    } else {
        alert('got empty message');
    }
}

function sendMessage(event) {
    event.preventDefault();

    let msg = $('#send_message_form_message');
    
    console.log('wysylanie wiadomosci: ' + msg.val() + ', dlugosc: ' + msg.val().length + ', na conv o id: ' + currentConversationId);
    if (msg.val().trim() !== '') {
        stompClient.send('/app/chat/' + currentConversationId, {}, JSON.stringify({
            'author': currentUser,
            'content': msg.val(),
            'sendingDate': Date.now()
        }));
    }

    msg.height(0).val('').focus();
}

function endTurn(gameId, turn, gameOver, boardState) {
    console.log('EndTurn:');
    console.log('    gameId: ' + gameId);
    console.log('    turn: ' + turn);
    console.log('    gameOver: ' + gameOver);
    console.log('    boardState: ' + boardState);

    console.log('koniec tury [' + currentUser + '], boardState: [' + boardState + '], wysylanie stanu na server');
    stompClient.send('/app/game/' + currentConversationId, {}, JSON.stringify({
        'gameId': gameId,
        'turn': turn,
        'gameOver': gameOver,
        'boardState': boardState,
        'sendingDate': Date.now()
    }));
}

