interface IFavoritesStorage {
  has (id: string): boolean

  add (id: string): Promise<void>

  remove (id: string): Promise<void>
}

class Favorites {
  protected favorites: IFavoritesStorage

  constructor (favorites: IFavoritesStorage) {
    this.favorites = favorites
  }

  public isFavored (id: string | null): boolean {
    if (!id) {
      return false
    }

    return this.favorites.has(id)
  }

  public async toggle (id: string | null) {
    if (!id) {
      return
    }

    if (!this.favorites.has(id)) {
      await this.favorites.add(id)
    } else {
      await this.favorites.remove(id)
    }
  }
}

export default Favorites
