import { ProposalDTO } from 'mysterium-tequilapi'
import { CountryListItem } from './country-picker'
import { Proposal } from '../../../libraries/favorite-proposal'

const getCountryListItemsFromProposals = (proposals?: ProposalDTO[]) => {
  if (!proposals) {
    return []
  }

  const countryList = proposals.map((proposalDto: ProposalDTO): CountryListItem => {
    const proposal = new Proposal(proposalDto, false)

    return {
      id: proposalDto.providerId,
      name: proposal.name,
      countryCode: proposal.getCountryCode(proposalDto)
    }
  })

  return countryList
}

export {
  getCountryListItemsFromProposals
}
