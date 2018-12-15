class FavoritesStorageMock {
  constructor (private favorites: string[]) {
  }

  public has (id: string): boolean {
    return this.favorites.indexOf(id) !== -1
  }
}

export default FavoritesStorageMock
