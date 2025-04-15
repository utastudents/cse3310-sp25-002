class Communication {
  constructor(dataManager) {
    this.dataManager = dataManager;
    this.listenForPlayerData();
  }

  listenForPlayerData() {
    window.addEventListener("message", (event) => {
      if (event.data && event.data.type === "playerData") {
        this.dataManager.setPlayer(event.data.payload);
      }
    });
  }

  sendPlayerAttributes(mode) {
    const player = this.dataManager.getPlayer();
    if (!player) return;
    window.postMessage({
      type: "matchRequest",
      payload: {
        playerID: player.id,
        username: player.username,
        mode: mode
      }
    }, "*");
  }
}
