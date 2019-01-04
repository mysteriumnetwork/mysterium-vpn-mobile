import { ProposalListItem } from '../components/proposal-picker/proposal-list-item'
import { QualityCalculator } from '../domain/quality-calculator'
import Proposal from '../models/proposal'
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
  private readonly qualityCalculator: QualityCalculator = new QualityCalculator()

  constructor (proposalList: IProposalList, favorites: IFavoritesStorage) {
    this.proposalList = proposalList
    this.favorites = favorites
  }

  public get proposals (): ProposalListItem[] {
    const proposals = this.proposalList.proposals
      .map((proposal: Proposal) => this.proposalToProposalItem(proposal))
      .sort(compareProposalItems)

    return proposals
  }

  private proposalToProposalItem (proposal: Proposal): ProposalListItem {
    return {
      providerID: proposal.providerID,
      countryCode: proposal.countryCode,
      countryName: proposal.countryName,
      isFavorite: this.favorites.has(proposal.providerID),
      quality: this.qualityCalculator.calculate(proposal.metrics)
    }
  }
}

function compareProposalItems (one: ProposalListItem, other: ProposalListItem): number {
  if (one.isFavorite && !other.isFavorite) {
    return -1
  } else if (!one.isFavorite && other.isFavorite) {
    return 1
  }

  const oneName = one.countryName || translations.UNKNOWN
  const otherName = other.countryName || translations.UNKNOWN

  if (oneName > otherName) {
    return 1
  } else if (oneName < otherName) {
    return -1
  }
  return 0
}

export default ProposalList
