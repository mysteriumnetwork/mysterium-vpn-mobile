interface IFavoritesStorage {
  has (id: string): boolean

  add (id: string): Promise<void>

  remove (id: string): Promise<void>

  addOnChangeListener (listener: () => void): void
}

class Favorites {
  constructor (private favoritesStorage: IFavoritesStorage) {}

  public async toggle (id: string | null) {
    if (!id) {
      return
    }

    if (!this.favoritesStorage.has(id)) {
      await this.favoritesStorage.add(id)
    } else {
      await this.favoritesStorage.remove(id)
    }
  }
}

export default Favorites
