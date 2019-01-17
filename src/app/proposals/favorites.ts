import { ProposalListItem } from '../components/proposal-picker/proposal-list-item'

interface IFavoritesStorage {
  has (proposal: Proposal): boolean
  add (proposal: Proposal): Promise<void>
  remove (proposal: Proposal): Promise<void>
}

interface Proposal {
  id: string,
  legacyId: string | null
}

class Favorites {
  constructor (private favoritesStorage: IFavoritesStorage) {}

  public async toggle (proposal: ProposalListItem) {
    if (!this.favoritesStorage.has(proposal)) {
      await this.favoritesStorage.add(proposal)
    } else {
      await this.favoritesStorage.remove(proposal)
    }
  }
}

export default Favorites
