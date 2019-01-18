import { ProposalListItem } from '../components/proposal-picker/proposal-list-item'

interface IFavoritesStorage {
  has (proposalId: string): boolean
  add (proposalId: string): Promise<void>
  remove (proposalId: string): Promise<void>
}

class Favorites {
  constructor (private favoritesStorage: IFavoritesStorage) {}

  public async toggle (proposal: ProposalListItem) {
    if (!this.favoritesStorage.has(proposal.id)) {
      await this.favoritesStorage.add(proposal.id)
    } else {
      await this.favoritesStorage.remove(proposal.id)
    }
  }
}

export default Favorites
