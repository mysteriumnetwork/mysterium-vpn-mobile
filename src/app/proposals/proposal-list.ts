import { IProposal } from '../components/proposal-picker/proposal'
import { FavoriteProposal } from '../domain/favorite-proposal'
import Proposal from '../domain/proposal'
import translations from '../translations'

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

function compareFavoriteProposals (one: FavoriteProposal, other: FavoriteProposal): number {
  if (one.isFavorite && !other.isFavorite) {
    return -1
  } else if (!one.isFavorite && other.isFavorite) {
    return 1
  }
  const oneName = one.countryName || translations.UNKNOWN
  const otherName = other.countryName || translations.UNKNOWN
  if (oneName > otherName) {
    return 1
  } else if (oneName < oneName) {
    return -1
  }
  return 0
}

export default ProposalList
