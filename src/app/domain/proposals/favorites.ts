interface FavoritesStorage {
  has (proposalId: string): boolean
  add (proposalId: string): Promise<void>
  remove (proposalId: string): Promise<void>
}

class Favorites {
  constructor (private favoritesStorage: FavoritesStorage) {}

  public async toggle (proposalId: string) {
    if (!this.favoritesStorage.has(proposalId)) {
      await this.favoritesStorage.add(proposalId)
    } else {
      await this.favoritesStorage.remove(proposalId)
    }
  }
}

export default Favorites
