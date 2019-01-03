import { IProposal } from '../components/proposal-picker/proposal'

class ProposalFilter {
  constructor (private proposals: IProposal[]) {
  }

  public filterByText (text: string): IProposal[] {
    let filteredProposals = this.proposals

    if (!text.trim().length) {
      return filteredProposals
    }

    filteredProposals = filteredProposals.filter((proposal: IProposal) => {
      return this.matchProposalNameOrId(proposal, text)
    })

    return filteredProposals
  }

  private matchProposalNameOrId (proposal: IProposal, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }
}

export default ProposalFilter
