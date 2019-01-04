import { IProposal } from '../../../../src/app/components/proposal-picker/proposal'
import Proposal from '../../../../src/app/models/proposal'
import { ProposalItem } from '../../../../src/app/models/proposal-item'
import ProposalFilter from '../../../../src/app/proposals/proposal-filter'
import proposalData from './proposal-data'

describe('ProposalFilter', () => {
  let proposals: IProposal[]
  let proposalFilter: ProposalFilter

  describe('.filter', () => {
    beforeEach(() => {
      proposals = proposalData.map(convertProposalToIProposal)
      proposalFilter = new ProposalFilter(proposals)
    })

    it('should find all', () => {
      expect(proposalFilter.filterByText('')).toHaveLength(8)
    })

    it('should find proposal label by country name', () => {
      expect(proposalFilter.filterByText('United')).toHaveLength(3)
    })

    it('should find proposal label by case insensitive country name', () => {
      expect(proposalFilter.filterByText('united')).toHaveLength(3)
    })

    it('should find proposal by partial id', () => {
      const list = proposalFilter.filterByText('x6')
      expect(list[0].providerID).toEqual('0x6')
      expect(list[0].countryCode).toEqual('it')
      expect(list).toHaveLength(1)
    })

    it('should return empty list when no matches are found', () => {
      expect(proposalFilter.filterByText('0x007')).toHaveLength(0)
    })
  })
})

const convertProposalToIProposal = (proposal: Proposal): IProposal => {
  return new ProposalItem(proposal, true, null)
}