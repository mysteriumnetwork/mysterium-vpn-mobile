import Proposal from '../../models/proposal'
import { ProposalItem } from '../../models/proposal-item'
import { EventNotifier } from '../observables/event-notifier'
import { QualityCalculator } from '../quality-calculator'

interface ProposalsStore {
  proposals: Proposal[]
  onChange (callback: () => void): void
}

interface FavoritesStorage {
  has (proposalId: string): boolean
  onChange (listener: Callback): void
}

class ProposalList {
  protected proposalsStore: ProposalsStore
  protected favorites: FavoritesStorage
  private readonly qualityCalculator: QualityCalculator = new QualityCalculator()
  private changeNotifier: EventNotifier = new EventNotifier()

  constructor (proposalsStore: ProposalsStore, favorites: FavoritesStorage) {
    this.proposalsStore = proposalsStore
    this.favorites = favorites

    this.receiveChangesFromDependencies()
  }

  public get proposals (): ProposalItem[] {
    const proposals = this.proposalsStore.proposals
      .map((proposal: Proposal) => this.proposalToProposalItem(proposal))
      .sort(compareProposalItems)

    return proposals
  }

  public onChange (callback: Callback) {
    this.changeNotifier.subscribe(callback)
  }

  private receiveChangesFromDependencies () {
    const notifyChange = () => this.changeNotifier.notify()
    this.favorites.onChange(notifyChange)
    this.proposalsStore.onChange(notifyChange)
  }

  private proposalToProposalItem (proposal: Proposal): ProposalItem {
    return {
      id: proposal.id,
      providerID: proposal.providerID,
      serviceType: proposal.serviceType,
      countryCode: proposal.countryCode,
      countryName: proposal.countryName,
      isFavorite: this.favorites.has(proposal.id),
      quality: this.qualityCalculator.calculate(proposal.metrics)
    }
  }
}

type Callback = () => void

function compareProposalItems (one: ProposalItem, other: ProposalItem): number {
  if (one.isFavorite && !other.isFavorite) {
    return -1
  } else if (!one.isFavorite && other.isFavorite) {
    return 1
  }

  return compareNames(one.countryName, other.countryName)
}

function compareNames (one: string | null, other: string | null): number {
  if (one === null && other === null) {
    return 0
  }
  if (one === null) {
    return 1
  }
  if (other === null) {
    return -1
  }

  if (one > other) {
    return 1
  } else if (one < other) {
    return -1
  }
  return 0
}

export default ProposalList
