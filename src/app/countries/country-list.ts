import { compareFavoriteProposals, FavoriteProposal } from '../../libraries/favorite-proposal'
import { ICountry } from '../components/country-picker/country'
import Proposal from '../domain/proposal'

interface IProposalList {
  proposals: Proposal[]
}

interface IFavoritesStorage {
  has (id: string): boolean
}

class CountryList {
  protected proposalList: IProposalList
  protected favorites: IFavoritesStorage

  constructor (proposalList: IProposalList, favorites: IFavoritesStorage) {
    this.proposalList = proposalList
    this.favorites = favorites
  }

  public get countries (): ICountry[] {
    const proposals = this.proposalList.proposals
      .map((proposal: Proposal) => this.proposalToFavoriteProposal(proposal))
      .sort(compareFavoriteProposals)

    return proposals
  }

  private proposalToFavoriteProposal (proposal: Proposal): FavoriteProposal {
    return new FavoriteProposal(
      proposal,
      this.favorites.has(proposal.providerID)
    )
  }
}

export default CountryList
