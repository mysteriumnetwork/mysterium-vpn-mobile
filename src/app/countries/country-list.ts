import { ProposalDTO } from 'mysterium-tequilapi'
import { compareProposals, Proposal } from '../../libraries/favorite-proposal'
import { ICountry } from '../components/country-picker/country'

interface IProposalList {
  proposals: ProposalDTO[]
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
      .map((dto: ProposalDTO) => this.dtoToProposal(dto))
      .sort(compareProposals)

    return proposals
  }

  private dtoToProposal (dto: ProposalDTO): Proposal {
    return new Proposal(
      dto,
      this.favorites.has(dto.providerId)
    )
  }
}

export default CountryList
