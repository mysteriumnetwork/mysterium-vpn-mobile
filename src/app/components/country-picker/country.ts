import { ProposalDTO } from 'mysterium-tequilapi'
import { Proposal } from '../../../libraries/favorite-proposal'

type Country = {
  id: string
  name: string,
  countryCode: string
}

const proposalToCountry = (proposalDto: ProposalDTO): Country => {
  const proposal = new Proposal(proposalDto, false)

  return {
    id: proposalDto.providerId,
    name: proposal.name,
    countryCode: proposal.getCountryCode(proposalDto)
  }
}

const proposalsToCountries = (proposals?: ProposalDTO[]): Country[] => {
  if (!proposals) {
    return []
  }

  return proposals.map(proposalToCountry)
}

export {
  proposalsToCountries,
  Country
}
