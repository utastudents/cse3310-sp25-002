// TODO: This variable is temporary and will be deleted in the implementation phase
var game_id;
// keep track of current player name for functions that are outside of the CheckersBoard class.
var game_display_current_player_name;
var game_display_current_player_id;

// this variable is used to check if the game board has been initialized
let game_display_checkers_board_initialized = false;
let checkerBoard = null;

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
        // this prevents spectators or non-current players from resigning
        if (checkerBoard.player !== checkerBoard.current_player) {
            game_display_popup_messages("You cannot resign because it's not your turn or you are spectating.");
            return;
        }
        connection.send(JSON.stringify({type: "resign", game_id: game_id, id: game_display_current_player_id, player: game_display_current_player_name}));
    } catch (error) {
        console.error("Error in game_display_checkers.js: ", error);
        game_display_popup_messages(`(gd) handle_resign: An error occurred while handling the game display. Please check the console.`);
    }
};

const offer_draw = () => {
    try{
        // this prevents spectators or non-current players from offering a draw
        if (checkerBoard.player !== checkerBoard.current_player) {
            game_display_popup_messages("You cannot offer a draw because it's not your turn or you are spectating.");
            return;
        }
        connection.send(JSON.stringify({type: "draw", game_id: game_id, id: game_display_current_player_id, player: game_display_current_player_name}));
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



const show_game_display = (connection, gameid, starting_player, player, player_color, player_id) => {
    // this element is used to display the game board
    let gameContainer = document.getElementById("game_display_container");

    if (!gameContainer){
        console.log("game display container not found");
        return;
    }
    gameContainer.classList.remove("hidden");
    gameContainer.classList.add("visible");

    if(!game_display_checkers_board_initialized){

        game_id = gameid;
        game_display_current_player_name = starting_player;
        game_display_current_player_id = player_id;
        // use the CheckersBoard class to create the game board and attach the class to the DOM
        checkerBoard = new CheckersBoard(connection, gameid, starting_player, player, player_color, player_id);
        // call the create_checkers_board method to create the game board
        checkerBoard.create_checkers_board();
        // attach all the event listeners to the game board. Note: this function is defined in the game_display_checkers.js file
        add_game_display_user_control_event_listener();
        // set the flag to true to indicate that the game board has been initialized. Note: this is used to prevent the game board from being initialized multiple times
        game_display_checkers_board_initialized = true;
    };

    if (checkerBoard.current_player === checkerBoard.player){
        document.getElementById("current-player").innerText = "It's your turn to make a move!";
    } else{
        document.getElementById("current-player").innerText = `Your opponent (${checkerBoard.current_player}) is going to make a move.`;
    }
    // document.getElementById("current-player").textContent = `Current Player: ${starting_player}`;
}

const hide_game_display = () => {
    // this element is used to display the game board
    let gameContainer = document.getElementById("game_display_container");

    if(!gameContainer){
        console.log("game display container not found");
        return;
    }
    if(!game_display_checkers_board_initialized){
        return;
    }
    gameContainer.classList.add("hidden");
    gameContainer.classList.remove("visible");
}


const game_display_handle_websocket_received_data = (connection, data) => {
    try{
        // This function handles the websocket data received from the java backend and updates the checkerboard accordingly.
        if(!data || !data?.type){
            return;
        };

        let game_display_event_types = ["valid_moves", "move_made_by_other_player_or_bot", "resign", "draw_offer", "draw_accept", "player_name_update", "notify_players", "show_game_display", "hide_game_display"];

        // this ignores any data that is not related to the game display.
        if(!game_display_event_types.includes(data.type)){ return; }

        if(!game_display_checkers_board_initialized && !(data.type === "show_game_display")){
            // if the game board is not initialized and the data type is not show_game_display, then return
            game_display_popup_messages(`(gd) game_display_handle_websocket_received_data: game board not initialized and data type is not show_game_display.`);
            return;
        };
        console.log("game display",data)

        if (data.type==="valid_moves") {
            // assuming that websocket sends the json string {"type":"valid_moves", "legal_moves":[[x1,y1],[x2,y2],...]}

            if (checkerBoard && checkerBoard.last_requested_moves) {
                checkerBoard.handle_valid_move_received_from_websocket(data);
            }

        } else if(data.type === "move_made_by_other_player_or_bot") {
            // assuming that websocket sends the json string {"type":"move_made_by_other_player_or_bot", "game_id": "GAME_ID_IF_PROVIDED", "player":"NAME OF PLAYER THAT MADE THE MOVE (STRING),"from": [move_from_x, move_from_y],"to": [move_to_x, move_to_y]"}
            //checks to make sure that it is a move from the opponent
            if(data.player != checkerBoard.player){
                // move_from_x, move_from_y, move_to_x, move_to_y = data.from[0], data.from[1], data.to[0], data.to[1];
                // this function is used to move the checkers piece from one square to another. This function is called when the opponent makes a move.
                checkerBoard.move_made_by_other_player_or_bot(data.from[0],data.from[1],data.to[0],data.to[1]);
            };

        } else if(data.type === "resign") {
            // assuming that websocket sends the json string {"type":"resign", "player":"NAME OF PLAYER THAT RESIGNED (STRING)"}
            alert(`${data.player} has resigned. The game is now over.`);

        } else if(data.type === "draw_offer") {
            // assuming that websocket sends the json string {"type":"draw_offer", "player":"NAME OF PLAYER THAT OFFERED THE DRAW (STRING)"}
            if(confirm(`${data.player} offered a draw, would you like to accept?`)) {
                connection.send(JSON.stringify({type: "draw_accept", game_id: game_id, id: game_display_current_player_id, player: game_display_current_player_name}));
            };
        } else if(data.type === 'draw_accept'){
            // assuming that websocket sends the json string {"type":"draw_accept"}
            alert("The draw has been accepted. The game is now over.");

        } else if(data.type === 'player_name_update'){
            // assuming that websocket sends the json string {"type":"player_name_update", "current_move":"NAME OF PLAYER THAT WILL MAKE NEXT MOVE (STRING)"}
            checkerBoard.update_current_player(data.current_move, data.id);

        } else if(data.type === 'notify_players'){
            // assuming that websocket sends the json string {"type":"notify_players", "message":"Game won/ Game Draw/ Connection issue/ Error message"}
            game_display_popup_messages(data.message);

        } else if(data.type === 'show_game_display') {
            // this function is used to show the game display. It is called when the game is started.
            console.log("game display initialized")
            show_game_display(connection, data.game_id, data.starting_player, data.player, data.player_color, data.playerId);

        } else if(data.type === 'hide_game_display') {
            hide_game_display();
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
    constructor(conn, g_id, starting_player, player, player_color, player_id) {
        this.checkers_board = [];
        this.selected_piece = null;
        // pass the websocket connection instance
        this.connection = conn;
        this.game_id = g_id;
        // the player who will make the next move
        this.current_player = starting_player;
        // this is used to keep track of the last clicked coordinate to 1) prevent user from clicking on the same square twice 2) to keep track of from and to coordinates when a piece is moved
        this.last_clicked_coordinate = null;
        // the player who is currently playing the game
        this.player = player;
        // the color of the player who is currently playing the game
        this.player_color = player_color;
        this.player_id = player_id;
        // flags for handling valid moves and the last clicked coordinate
        this.last_requested_moves = null;
    }

    //function is used to get the color of the piece
    get_piece_color(piece_type){
        if (piece_type === 'w' || piece_type === 'W')
            return 'W';
        if (piece_type === 'b' || piece_type === 'B')
            return 'B';
        return null;
    }


    update_current_player(player, player_id) {
        // Update the UI to show whose turn it is

        try{
            this.current_player = player;
            this.player_id = player_id;
            game_display_current_player_name = player;
            game_display_current_player_id = player_id;

            if (this.current_player === this.player){
                document.getElementById("current-player").innerText = "It's your turn to make a move!";
            } else{
                document.getElementById("current-player").innerText = `Your opponent (${this.current_player}) is going to make a move.`;
            }
        }
        catch (error){
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages('(gd) update_current_player: An error occurred while handling the game display. Please check the console.');
        }
    }


    /*
        handle_checkers_piece_click()
        Rules referenced from: https://en.wikipedia.org/wiki/English_draughts
    */
    async handle_checkers_piece_click(x, y) {
        try{

            // if it's not the player's turn, do nothing. Note: We added this to prevent the user from clicking on the board when it's not their turn. Or when a bot or opponent makes a move which shouldn't be registered as the user's move.
            if(this.player !== this.current_player) return;

            const square = this.checkers_board.find(sq => sq.x === x && sq.y === y);
            if (!square) return;
            // either "b" or "w" or "." Note: . means an empty square with no piece
            const checkers_piece_type = square.el.getAttribute("data-piece");

            // if piece if first clicked
            if (!this.selected_piece && checkers_piece_type !== ".") {
                this.selected_piece = {x, y, type: checkers_piece_type};
                this.last_clicked_coordinate = {x, y};
                await this.show_possible_moves(x, y);
            }
            // if a piece is already selected and the user clicks on another square
            else if (this.selected_piece) {
                // If clicking on the same piece, deselect it
                if (this.selected_piece.x === x && this.selected_piece.y === y) {
                    this.selected_piece = null;
                    this.hide_possible_moves();
                }
                // if user has clicked on a valid move
                else if (await this.is_valid_move(this.selected_piece.x, this.selected_piece.y, x, y)) {
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
                        await this.show_possible_moves(x, y);
                    }
                }

            }
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) handle_checkers_piece_click: An error occurred while handling the game display. Please check the console.`);
        }
    }

    //move_made_by_other_player_or_bot() sets a flag to call move_checkers_piece() without sending a move request to the backend.
    move_made_by_other_player_or_bot(move_from_x, move_from_y, move_to_x, move_to_y){
        this.move_checkers_piece(move_from_x, move_from_y, move_to_x, move_to_y);
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
                console.log({type: "move", game_id: this.game_id, player: this.current_player, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}});
                //Does not send a move request to the backend if it is the opponent's turn
                if(this.player === this.current_player){
                    this.connection.send(JSON.stringify({type: "move", game_id: this.game_id, id: this.player_id, player: this.current_player, square: {"from":[move_from_x, move_from_y],"to":[move_to_x, move_to_y]}}));
                }
            }

        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) move_checkers_piece: An error occurred while handling the game display. Please check the console.`);
        }
    }

    rotateBoardIfBlack() {
        try {
            if (this.player_color === "B") {
                // rotate the entire game board container
                const gameBoard = document.querySelector(".game");
                gameBoard.style.transform = "rotate(180deg)";

                // rotate each piece in the opposite direction to keep them upright
                this.checkers_board.forEach(square => {
                    const piece = square.el.querySelector('.piece');
                    if (piece) {
                        piece.style.transform = "rotate(180deg)";
                    }
                });

                // also rotate the king text to keep it readable
                const kings = document.querySelectorAll('.King');
                kings.forEach(king => {
                    king.style.transform = "rotate(180deg)";
                });
            }
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) rotateBoardIfBlack: An error occurred while rotating the board. Please check the console.`);
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
                this.rotateBoardIfBlack();
            } catch (error) {
                console.error("Error in game_display_checkers.js: ", error);
                game_display_popup_messages(`(gd) create_checkers_board: An error occurred while handling the game display. Please check the console.`);
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
            this.rotateBoardIfBlack();
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) update_board_style: An error occurred while handling the game display. Please check the console.`);
        }
    }


    /*
        show_possible_moves() relies on the return_allowed_moves() function to determine if a move is valid.
        This function is custom implemented.
    */
    async show_possible_moves(x, y) {
        try{
            this.hide_possible_moves();
            let valid_moves = await this.return_allowed_moves(x, y);
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
    async is_valid_move(move_from_x, move_from_y, move_to_x, move_to_y) {
        try{
            const valid_moves = await this.return_allowed_moves(move_from_x, move_from_y);
            return valid_moves.some(move => move.x === move_to_x && move.y === move_to_y);
        } catch (error) {
            console.error("Error in game_display_checkers.js: ", error);
            game_display_popup_messages(`(gd) is_valid_move: An error occurred while handling the game display. Please check the console.`);
            return false;
        }
    }

    handle_valid_move_received_from_websocket(data){
        //checks if recieved data type is valid_moves
        if (data.type === "valid_moves" && this.last_requested_moves){
            //destructure the resolver func and req piece color
            const { resolver, requested_piece_color } = this.last_requested_moves;
            //gets color of selected piece
            const selected_piece_color = this.get_piece_color(this.selected_piece.type);

            const filtered_legal_moves = (data.legal_moves || []).filter(move =>{
                const [dest_x, dest_y] = move;

                //find the destination square on the board
                const dest_square = this.checkers_board.find(sq => sq.x === dest_x && sq.y === dest_y);
                //if destination square is not found, , show error and skip move
                if (!dest_square){
                    game_display_popup_messages(`filtered_legal_moves: Destination square [${dest_x}, ${dest_y}] not found in checkers_board`);
                    console.log(`Destination square [${dest_x}, ${dest_y}] not found in checkers_board.`);
                    return false;
                }
                //get the piece type and color at the destination square
                const dest_piece_type = dest_square.el.getAttribute("data-piece");
                const dest_piece_color = this.get_piece_color(dest_piece_type);

                //keep the move if one of the two is true:
                //the destination square is empty
                //the destination square has a piece, and the color is different from the selected piece's color
                return dest_piece_type === '.' || dest_piece_color !== requested_piece_color;
            });

            //map the filtered legal moves into array
            const move_object = filtered_legal_moves.map(move => ({ x: move[0], y: move[1]}));

            //resolves the list of valid moves
            resolver(move_object);

            //clears the last requested moves to avoid memory leaks
            this.last_requested_moves = null;
        }
    }
    


    /*
        return_allowed_moves relies on the backend to provide the allowed moves for a piece at a given position.
        This function is custom implemented.
    */
    return_allowed_moves(x, y) {
        return new Promise((resolve) => {
            try {
                const square = this.checkers_board.find(sq => sq.x === x && sq.y === y);
                if (!square) {
                    resolve([]);
                    return;
                }
                const piece_type_at_request = square.el.getAttribute("data-piece");
                const piece_color_at_request = this.get_piece_color(piece_type_at_request);

                this.last_requested_moves = { resolver: resolve, requested_piece_color: piece_color_at_request };

                // TODO: This needs to be handled by the java backend since this involves making game logic
                this.connection.send(JSON.stringify({type: "get_allowed_moves", game_id: this.game_id, id: this.player_id, player: this.current_player, square: [x, y] }));
                // we do not receive a response from the request to the websocket, so i prevented a non thread blocking mechanism.
                // wait for upto 7 seconds until we get the valid moves
                setTimeout(() => {
                    if (this.last_requested_moves && this.last_requested_moves.resolver === resolve) {
                        console.log("waiting for valid moves....");
                        resolve([]);
                        this.last_requested_moves = null;
                    }
                }, 7000);

            } catch (error) {
                console.error("Error in game_display_checkers.js: ", error);
                game_display_popup_messages(`(gd) return_allowed_moves: An error occurred while handling the game display. Please check the console.`);
                resolve([]);
                this.last_requested_moves = null;
            }
        });
    }
}
