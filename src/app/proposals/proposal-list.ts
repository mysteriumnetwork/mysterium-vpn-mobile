import { IProposal } from '../components/proposal-picker/proposal'
import { compareFavoriteProposals, FavoriteProposal } from '../domain/favorite-proposal'
import Proposal from '../domain/proposal'

interface IProposalList {
  proposals: Proposal[]
}

interface IFavoritesStorage {
  has (id: string): boolean
}

class ProposalList {
  protected proposalList: IProposalList
  protected favorites: IFavoritesStorage

  constructor (proposalList: IProposalList, favorites: IFavoritesStorage) {
    this.proposalList = proposalList
    this.favorites = favorites
  }

  public get proposals (): IProposal[] {
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

export default ProposalList
