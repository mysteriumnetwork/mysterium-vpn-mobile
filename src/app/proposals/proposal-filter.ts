import { ProposalListItem } from '../components/proposal-picker/proposal-list-item'

class ProposalFilter {
  constructor (private proposals: ProposalListItem[]) {
  }

  public filterByText (text: string): ProposalListItem[] {
    let filteredProposals = this.proposals

    if (!text.trim().length) {
      return filteredProposals
    }

    filteredProposals = filteredProposals.filter((proposal: ProposalListItem) => {
      return this.matchProposalNameOrId(proposal, text)
    })

    return filteredProposals
  }

  private matchProposalNameOrId (proposal: ProposalListItem, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }
}

export default ProposalFilter
