// TODO: The game manager team is responsible for providing us with the game_id. This unique id will be used to send information to the java webseckt server

let game_id;
let currentPlayer;
let connection;

// TODO: The game manager team is responsible for using the provided information to handle page transimition
const handle_resign = () =>  connection.send(JSON.stringify({type: "resign", game_id: game_id, player: currentPlayer}));
const offer_draw = () => connection.send(JSON.stringify({type: "draw", game_id: game_id, player: currentPlayer}));

const register_buttons_to_event_listener = (element, event_type, handler) => {
// Note: I am adding this function to prevent event duplication
element.removeEventListener(event_type, handler);
element.addEventListener(event_type, handler);
};

const add_game_display_user_control_event_listener = () => {
const resign_button = document.getElementById("resign_buttom");
const offer_draw_button = document.getElementById("offer_draw_button");
register_buttons_to_event_listener(resign_button, "click", handle_resign);
register_buttons_to_event_listener(offer_draw_button, "click", offer_draw);
};



class CheckersBoard {
constructor(conn, g_id, starting_player) {
    this.board = [];
    this.selected_piece = null;
    connection = conn;
    game_id = g_id;
    currentPlayer = starting_player;
    // pass the websocket connection instance
    this.connection = conn;
    this.game_id = g_id;
    this.currentPlayer = starting_player;
    // this is used to keep track of the last clicked coordinate to 1) prevent user from clicking on the same square twice 2) to keep track of from and to coordinates when a piece is moved
    this.last_clicked_coordinate = null;
}

update_current_player(player) {
    // TODO: The game manager will call this method from the class to change the current player after each move
    this.currentPlayer = player;
    currentPlayer = player;
    document.getElementById("current-player").innerText = `Current Player: ${player}`;
}

handle_checkers_piece_click(x, y) {
    const square = this.board.find(sq => sq.x === x && sq.y === y);
    if (!square) return;
    // either "b" or "w" or "." Note: . means an empty square with no piece
    const checkers_piece_type = square.el.getAttribute("data-piece");

    // if piece if first clicked
    if (!this.selected_piece && checkers_piece_type !== ".") {
        this.selected_piece = {x, y, type: checkers_piece_type};
        this.last_clicked_coordinate = {x, y};
        this.show_possible_moves(x, y);
    }
    // if a piece is already selected and the user clicks on another square
    else if (this.selected_piece) {
        // If clicking on the same piece, deselect it
        if (this.selected_piece.x === x && this.selected_piece.y === y) {
            this.selected_piece = null;
            this.hide_possible_moves();
        }
        // if user has clicked on avalid move
        else if (this.is_valid_move(this.selected_piece.x, this.selected_piece.y, x, y)) {
            this.move_checkers_piece(this.selected_piece.x, this.selected_piece.y, x, y);
            // reset piece selection
            this.selected_piece = null;
            this.hide_possible_moves();
        }
        // if user clicked in an invalid square
        else {
            this.selected_piece = null;
            this.hide_possible_moves();
            // if user clicks on another piece then select that piece
            if (checkers_piece_type !== ".") {
                this.selected_piece = {x, y, type: checkers_piece_type};
                this.last_clicked_coordinate = {x, y};
                this.show_possible_moves(x, y);
            }
        }
    }
}

move_checkers_piece(move_from_x, move_from_y, move_to_x, move_to_y) {
    const move_from_square = this.board.find(sq => sq.x === move_from_x && sq.y === move_from_y);
    const move_to_square = this.board.find(sq => sq.x === move_to_x && sq.y === move_to_y);

    if (move_from_square && move_to_square) {
        const checkers_piece_type = move_from_square.el.getAttribute("data-piece");
        move_from_square.el.setAttribute("data-piece", ".");

        // if the piece reaches the end of the board, it should be kinged
        if ((checkers_piece_type === 'b' && move_to_x === 7) || (checkers_piece_type === 'w' && move_to_x === 0)) {
            // change piece type to (uppercase, W or B to indicate this)
            move_to_square.el.setAttribute("data-piece", checkers_piece_type.toUpperCase());
        } else {
            move_to_square.el.setAttribute("data-piece", checkers_piece_type);
        }
        // Change turns after a move
        console.log({type: "move", game_id: this.game_id, player: this.currentPlayer, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}});
        this.connection.send(JSON.stringify({type: "move", game_id: this.game_id, player: this.currentPlayer, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}}));
    }
}

hide_possible_moves() {
    this.board.forEach(square => {
        square.el.classList.remove('show-valid-moves');
    });
}

show_possible_moves(x, y) {
    const valid_moves = this.return_allowed_moves(x, y);
    this.board.forEach((square) => {
        const is_valid_move = valid_moves.some(move => move.x === square.x && move.y === square.y);
        square.el.classList.toggle('show-valid-moves', is_valid_move);
    });
}

is_valid_move(move_from_x, move_from_y, move_to_x, move_to_y) {
    const valid_moves = this.return_allowed_moves(move_from_x, move_from_y);
    return valid_moves.some(move => move.x === move_to_x && move.y === move_to_y);
}

async create_checkers_board() {
    const checkers_board_ = document.querySelector(".game");
    checkers_board_.innerHTML = '';
    this.board = [];

    for (let x = 0; x < 8; x++) {
        let checkers_board_row = document.createElement("div");
        checkers_board_row.classList.add("row");
        checkers_board_.appendChild(checkers_board_row);

        for (let y = 0; y < 8; y++) {
            let checkers_board_square = document.createElement("div");
            checkers_board_square.classList.add("square");

            // Checkerboard pattern - dark squares should be where pieces go
            const is_dark_square = (x + y) % 2 !== 0;
            checkers_board_square.classList.toggle("odd", !is_dark_square);

            // Create the pieces
            if (x < 3 && is_dark_square) {
                checkers_board_square.setAttribute("data-piece", "b"); // black checker piece
            } else if (x > 4 && is_dark_square) {
                checkers_board_square.setAttribute("data-piece", "w"); // white checker piece
            } else {
                checkers_board_square.setAttribute("data-piece", "."); // empty square
            }

            // this piece will turn into a king once it reaches the end of the board. Until then I have kept it hidden
            checkers_board_square.innerHTML = "<div class='piece'><div class='King'>K</div></div>";

            const handle_square_click = (e) => this.handle_checkers_piece_click(x, y);
            checkers_board_square.addEventListener("click", handle_square_click);

            checkers_board_row.appendChild(checkers_board_square);
            this.board.push({ el: checkers_board_square, x, y });
        }
    }
}

return_allowed_moves(x, y) {
    const square = this.board.find(sq => sq.x === x && sq.y === y);
    if (!square) return [];

    const checkers_piece_type = square.el.getAttribute("data-piece");
    const moves = [];
    // checkers_piece_type = "b" or "w"
    // TODO: This needs to be handled by the java backend since this involves making game logic
    this.connection.send(JSON.stringify({type: "get_allowed_moves", game_id: this.game_id, player: this.currentPlayer, square: [x, y]}));
    // later we will implement an event listener that listen to the allowed moves and update the moves array
    return []; // an array of possibly [x,y] positions that this piece can more to
}
}

const initialize_game_display = async() => {
window.checkerBoard = new CheckersBoard();
// Initialize the board
await checkerBoard.create_checkers_board();
// register the resign and draw button to the event listener
add_game_display_user_control_event_listener();
}

// TODO: This function should be called to start the game display. Page manager team is responsible for calling this function.
initialize_game_display();
//game display js end
