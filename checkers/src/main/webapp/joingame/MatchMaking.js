class Matchmaker {
    constructor() {
        this.queue = []; // Queue for players, do I need waitlist class?
    }


    addToQueue(player) {
        this.queue.push(player);
        console.log('${player} added to queue.');
        this.tryMatchPlayers();
    }


    tryMatchPlayers() {
        if (this.queue.length >= 2) {
            // match 2 human players
            let player1 = this.queue.shift();
            let player2 = this.queue.shift();
            console.log('Match created: ${player1} vs ${player2}');


            // Send to communication
            this.sendMatchInfo(player1, player2, "Player");
        }
    }


    requestBotMatch(player) {
        console.log('${player} wants to play against a bot.');

        // Send to communication
        this.sendMatchInfo(player, "Bot", "Bot");
    }


    matchWithBot(player){


        console.log('Match created: ${player} vs ${bot}');


        // Send to communication
        this.sendMatchInfo(player, bot, "Bot");
    }


}
