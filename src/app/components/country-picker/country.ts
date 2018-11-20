import { Proposal } from '../../../libraries/favorite-proposal'

type Country = {
  id: string
  name: string,
  countryCode: string,
  isFavored: boolean
}

const proposalToCountry = (proposal: Proposal): Country => {
  return {
    id: proposal.providerID,
    name: proposal.name,
    countryCode: proposal.countryCode,
    isFavored: proposal.isFavorite
  }
}

const proposalsToCountries = (proposals?: Proposal[]): Country[] => {
  if (!proposals) {
    return []
  }

  return proposals.map(proposalToCountry)
}

export {
  proposalsToCountries,
  Country
}
