// TODO: This variable is temporary and will be deleted in the implementation phase
var game_id;
// keep track of current player name for functions that are outside of the CheckersBoard class.
var game_display_current_player_name;


/*
    The logic for handling resign and draw button is extremely simple. Removing the event listener and adding it again prevents event duplication. This is a very common practice in the industry.
*/
const game_display_popup_messages = (message) =>{
    if(!message){
        return;
    }
    // this function handles the popup messages for the game display. All errors/alters/announcements are handled here.
    alert(message);
}



const handle_resign = () => {
    try{
        connection.send(JSON.stringify({type: "resign", game_id: game_id, player: game_display_current_player_name}));
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) handle_resign: An error occurred while handling the game display. Please check the console.`);
    }
};

const offer_draw = () => {
    try{
        connection.send(JSON.stringify({type: "draw", game_id: game_id, player: game_display_current_player_name}));
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) offer_draw: An error occurred while handling the game display. Please check the console.`);
    }
};

const register_buttons_to_event_listener = (element, event_type, handler) => {
    // Note: I am adding this function to prevent event duplication
    try{
        element.removeEventListener(event_type, handler);
        element.addEventListener(event_type, handler);
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) register_buttons_to_event_listener: An error occurred while handling the game display. Please check the console.`);
    }

};

const add_game_display_user_control_event_listener = () => {
    try{
        const resign_button = document.getElementById("resign_buttom");
        const offer_draw_button = document.getElementById("offer_draw_button");
        register_buttons_to_event_listener(resign_button, "click", handle_resign);
        register_buttons_to_event_listener(offer_draw_button, "click", offer_draw);
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) add_game_display_user_control_event_listener: An error occurred while handling the game display. Please check the console.`);
    }

};


const game_display_handle_websocket_received_data = (checkerBoard, data) => {
    try{
        // This function handles the websocket data received from the java backend and updates the checkerboard accordingly.
        if(!data){
            return;
        }
        if (data.type==="valid_moves" && data.legal_moves.length > 0) {
            // assuming that websocket sends the json string {"type":"valid_moves", "legal_moves":[[x1,y1],[x2,y2],...]}
            checkerBoard.received_coords = data.legal_moves;

        } else if(data.type === "resign") {
            // assuming that websocket sends the json string {"type":"resign", "player":"NAME OF PLAYER THAT RESIGNED (STRING)"}
            alert(`${data.player} has resigned. The game is now over.`);

        } else if(data.type === "draw_offer") {
            // assuming that websocket sends the json string {"type":"draw_offer", "player":"NAME OF PLAYER THAT RESIGNED (STRING)"}
            if(confirm(`${data.player} offered a draw, would you like to accept?`)) {
                connection.send(JSON.stringify({type: "draw_accept", game_id: game_id, player: game_display_current_player_name}));
            };

        } else if(data.type === 'draw_accept'){
            // assuming that websocket sends the json string {"type":"draw_accept"}
            alert("The draw has been accepted. The game is now over.");

        } else if(data.type === 'player_name_update'){
            // assuming that websocket sends the json string {"type":"player_name_update", "current_move":"NAME OF PLAYER THAT WILL MAKE NEXT MOVE (STRING)"}
            checkerBoard.update_player_name(data.current_move);
        } else if(data.type === 'notify_players'){
            // assuming that websocket sends the json string {"type":"notify_players", "message":"Game won/ Game Draw/ Connection issue/ Error message"}
            game_display_popup_messages(data.message);
        }
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) game_display_handle_websocket_received_data: An error occurred while handling the game display. Please check the console.`);
    }

}



class CheckersBoard {
    /*
        We have put the game display logic under the CheckersBoard class to handle all logic related to displaying the board. This will help in creating as many instances of checker board as needed.
    */
    constructor(conn, g_id, starting_player) {
        this.checkers_board = [];
        this.selected_piece = null;
        connection = conn;
        game_id = g_id;
        game_display_current_player_name = starting_player;
        // pass the websocket connection instance
        this.connection = conn;
        this.game_id = g_id;
        this.currentPlayer = starting_player;
        // this is used to keep track of the last clicked coordinate to 1) prevent user from clicking on the same square twice 2) to keep track of from and to coordinates when a piece is moved
        this.last_clicked_coordinate = null;
        this.received_coords = [];
    }

    
    update_current_player(player) {
        // Update the UI to show whose turn it is
        try{
            this.currentPlayer = player;
            game_display_current_player_name = player;
            document.getElementById("current-player").innerText = `Current Player: ${player}`;
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) update_current_player: An error occurred while handling the game display. Please check the console.`);
        }

    }



    /*
        handle_checkers_piece_click()
        Rules referenced from: https://en.wikipedia.org/wiki/English_draughts
    */
    handle_checkers_piece_click(x, y) {
        try{
            const square = this.checkers_board.find(sq => sq.x === x && sq.y === y);
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
                // if user has clicked on a valid move
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
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) handle_checkers_piece_click: An error occurred while handling the game display. Please check the console.`);
        }
    }




    /*
        move_checkers_piece() simply moves a checkers piece from one square to another. This function is called once the java backend has confirmed that the move is valid.
        Therefore, there is no complex logic here. This function simply moves the piece and sends the information to the java backend via ws.
        This function is also custom implemented.
    */
    move_checkers_piece(move_from_x, move_from_y, move_to_x, move_to_y) {
        try{
            let move_from_square = this.checkers_board.find(sq => sq.x === move_from_x && sq.y === move_from_y);
            let move_to_square = this.checkers_board.find(sq => sq.x === move_to_x && sq.y === move_to_y);

            if (move_from_square && move_to_square) {
                // https://developer.mozilla.org/en-US/docs/Web/API/Element/getAttribute
                let checkers_piece_type = move_from_square.el.getAttribute("data-piece");
                move_from_square.el.setAttribute("data-piece", ".");

                move_to_square.el.style.background = "rgb(209, 150, 111)";
                move_to_square.el.dataset.shownStep = "true";

                this.checkers_board.forEach(square => {
                    if (square !== move_to_square && square.el.dataset.shownStep === "true") {
                        square.el.style.background = square.el.dataset.originalBackground;
                        square.el.dataset.shownStep = "false";
                    }
                });

                // if the piece reaches the end of the board, it should be kinged
                if ((checkers_piece_type === 'b' && move_to_x === 7) || (checkers_piece_type === 'w' && move_to_x === 0)) {
                    // change piece type to (uppercase, W or B to indicate this)
                    // https://developer.mozilla.org/en-US/docs/Web/API/Element/setAttribute
                    move_to_square.el.setAttribute("data-piece", checkers_piece_type.toUpperCase());
                } else {
                    move_to_square.el.setAttribute("data-piece", checkers_piece_type);
                }

                this.update_board_style();
                // relay the move to the backend through the ws connection
                console.log({type: "move", game_id: this.game_id, player: this.currentPlayer, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}});
                this.connection.send(JSON.stringify({type: "move", game_id: this.game_id, player: this.currentPlayer, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}}));

            }

        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) move_checkers_piece: An error occurred while handling the game display. Please check the console.`);
        }
    }





    // custom css styling for the checkers board. We use this function to update the style of the board after every move. This function uses standard css styles and the design (colors, size, shape) was custom made by us.
    update_board_style() {
        try{
            this.checkers_board.forEach(square => {
                const pieceElement = square.el.querySelector('.piece');
                const kingElement = square.el.querySelector('.King');
                const pieceType = square.el.getAttribute('data-piece');
                pieceElement.style.borderRadius = "50%";
                pieceElement.style.position = "relative";
                pieceElement.style.width = "30px";
                pieceElement.style.height = "30px";
                pieceElement.style.border = "2px solid #333";
                pieceElement.style.marginTop = "3px";
                pieceElement.style.marginLeft = "3px";
                pieceElement.style.display = "inline-block";
                pieceElement.style.cursor = "grabbing";
                pieceElement.style.backgroundImage = "radial-gradient(circle at 30% 30%, rgba(255,255,255,0.4) 10%, transparent 60%)";

                // style for the king piece
                kingElement.style.display = "none";
                kingElement.style.fontFamily = "sans-serif";
                kingElement.style.fontWeight = "bold";
                kingElement.style.fontSize = "24px";
                kingElement.style.textAlign = "center";
                kingElement.style.position = "absolute";
                kingElement.style.width = "100%";

                // We added this to change style based on piece type
                if (pieceType === 'b' || pieceType === 'B') {
                    // b = black, B= Black king piece
                    pieceElement.style.backgroundColor = "#222";
                    pieceElement.style.boxShadow = "inset 0 0 8px rgba(0,0,0,0.8)";
                    if (pieceType === 'B') {
                        kingElement.style.display = "block";
                    }
                } else if (pieceType === 'w' || pieceType === 'W') {
                    // w = white, W = White king piece
                    pieceElement.style.backgroundColor = "#eee";
                    pieceElement.style.boxShadow = "inset 0 0 5px rgba(0,0,0,0.3)";
                    if (pieceType === 'W') {
                        kingElement.style.display = "block";
                    }
                } else if (pieceType === '.') {
                    pieceElement.style.display = "none";
                }

            });
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) update_board_style: An error occurred while handling the game display. Please check the console.`);
        }
    }



    /*
        show_possible_moves() relies on the return_allowed_moves() function to determine if a move is valid.
        This function is custom implemented.
    */
    show_possible_moves(x, y) {
        try{
            let valid_moves = this.return_allowed_moves(x, y);
            this.checkers_board.forEach((square) => {
                let check_valid_move = valid_moves.some(move => move.x === square.x && move.y === square.y);
                if (check_valid_move) {
                    square.el.style.background = "rgba(190, 228, 229, 0.5)";
                    square.el.dataset.highlighted = "true";
                }
            });
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) show_possible_moves: An error occurred while handling the game display. Please check the console.`);
        }
    }



    /*
        Simple function that simply removes a class from the squares to hide the best possible moves for a clicked checkers piece. This function is custom implemented.
    */
    hide_possible_moves() {
        try{
            this.checkers_board.forEach(square => {
                if (square.el.dataset.highlighted === "true") {
                    square.el.style.background = square.el.dataset.originalBackground;
                    square.el.dataset.highlighted = "false";
                }
            });
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) hide_possible_moves: An error occurred while handling the game display. Please check the console.`);
        }
    }




    /*
        is_valid_move() relies on the return_allowed_moves() function (which relies on the java backend) to determine if a move is valid.
        This function is custom implemented. It simply displays all possible moves for a clicked checkers pieces.
    */
    is_valid_move(move_from_x, move_from_y, move_to_x, move_to_y) {
        try{
            const valid_moves = this.return_allowed_moves(move_from_x, move_from_y);
            return valid_moves.some(move => move.x === move_to_x && move.y === move_to_y);
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) is_valid_move: An error occurred while handling the game display. Please check the console.`);
        }
    }





    /*
    Reference:
        create_checkers_board()
        -Inspired by: https://github.com/renerpdev/checkers/blob/master/src/js/game-controller.js (Inspiration, in this case, is the idea of using two for loops to create the board)
        -License: MIT (https://github.com/renerpdev/checkers?tab=MIT-1-ov-file)
        -Author: renerpdev
        -The logic for generating an 8x8 checkers board and placing pieces is based on this source.
        -However, this implementation is entirely rewritten with a different structure and approach.

        Key Differences:
        -A board array (`this.checkers_board`) to track pieces dynamically.
        -Uses `data-piece` attributes to store piece types for easy DOM manipulation.
        -Click event handlers are attached differently.
        -The only part reused directly is the basic structure of the `for` loops that iterate over the board rows and squares (inspiration is the idea of using for loops to dynamicall create divs/rows). This excludes the logic to add css styles, event listeners, and the logic to determine the piece type for each square.
    */
    async create_checkers_board() {
        try{

            // this function creates the checkers board using two for loops and custom css styles directly attached to each squares using standard css styles.
            const checkers_board_ = document.querySelector(".game");
            checkers_board_.innerHTML = '';
            this.checkers_board = [];

            checkers_board_.style.display = "flex";
            checkers_board_.style.flexDirection = "column";

            for (let x = 0; x < 8; x++) {
                let checkers_board_row = document.createElement("div");
                checkers_board_row.style.display = "flex";
                checkers_board_row.style.height = "41px";
                checkers_board_.appendChild(checkers_board_row);

                for (let y = 0; y < 8; y++) {
                    let checkers_board_square = document.createElement("div");

                    checkers_board_square.style.border = "1px solid #333";
                    checkers_board_square.style.width = "41px";
                    checkers_board_square.style.height = "41px";
                    checkers_board_square.style.position = "relative";
                    checkers_board_square.style.cursor = "pointer";
                    checkers_board_square.style.boxSizing = "border-box";

                    // create the game squares with alternating color
                    const is_dark_square = (x + y) % 2 !== 0;
                    checkers_board_square.style.background = is_dark_square ? "#866442" : "#EEE";
                    checkers_board_square.dataset.originalBackground = is_dark_square ? "#866442" : "#EEE";

                    // data-piece will store the type of piece on the square
                    if (x < 3 && is_dark_square) {
                        checkers_board_square.setAttribute("data-piece", "b"); // black checker piece
                    } else if (x > 4 && is_dark_square) {
                        checkers_board_square.setAttribute("data-piece", "w"); // white checker piece
                    } else {
                        checkers_board_square.setAttribute("data-piece", "."); // empty square
                    }

                    // this piece will turn into a king once it reaches the end of the board. Until then I have kept it hidden
                    checkers_board_square.innerHTML = "<div class='piece'><div class='King'>K</div></div>";

                    let handle_square_click = (e) => this.handle_checkers_piece_click(x, y);
                    checkers_board_square.addEventListener("click", handle_square_click);

                    checkers_board_row.appendChild(checkers_board_square);
                    this.checkers_board.push({ el: checkers_board_square, x, y });
                };
            };
            // sanitize the board to remove any previous styles and make sure that each piece is displayed correctly
            this.update_board_style();
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) create_checkers_board: An error occurred while handling the game display. Please check the console.`);
        }
    }




    //This function checks an array to make sure that it is a valid set of co-ordinates for the board
    allowed_moves_validation(moves){
        try{
            // initially the moves array is empty, so this function returns true unless we return false when the array is empty. This causes the while loop to fail. Adding this if statement at the begining, makes sure that the while loop is running until the moves array is filled with data.
            if (!moves || moves?.length==0){
                return false;
            };

            for(let i=0;i<moves.length;i++){
                if(moves[i].length!=2){
                    return false;
                };
                for(let j=0;j<2;i++){
                    if(!Number.isInteger(moves[i][j]) || !(0<=moves[i]<8)){
                        return false;
                    };
                };
            };
            return true;
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) allowed_moves_validation: An error occurred while handling the game display. Please check the console.`);
        }

    }


    /*
        return_allowed_moves relies on the backend to provide the allowed moves for a piece at a given position.
        This function is custom implemented.
    */
    return_allowed_moves(x, y) {
        try{
            const square = this.checkers_board.find(sq => sq.x === x && sq.y === y);
            if (!square) return [];

            // const checkers_piece_type = square.el.getAttribute("data-piece");
            // let moves = [];
            // checkers_piece_type = "b" or "w"
            // TODO: This needs to be handled by the java backend since this involves making game logic
            this.connection.send(JSON.stringify({type: "get_allowed_moves", game_id: this.game_id, player: this.currentPlayer, square: [x, y]}));

            // this function waits for the ws to provide the allowed moves for a piece at a given position.
            while(!this.allowed_moves_validation(this.received_coords)){
                //this loop waits for this.received_coords to be filled with data
            };

            //Returns an array of possible moves and resets the class-level variable in preparation for the next call.
            moves = this.received_coords;
            this.received_coords = [];
            return moves;

        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) return_allowed_moves: An error occurred while handling the game display. Please check the console.`);
        }
    };
}


