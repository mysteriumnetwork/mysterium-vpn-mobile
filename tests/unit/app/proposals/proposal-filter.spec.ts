import { ProposalListItem } from '../../../../src/app/components/proposal-picker/proposal-list-item'
import Proposal from '../../../../src/app/models/proposal'
import ProposalFilter from '../../../../src/app/proposals/proposal-filter'
import proposalData from './proposal-data'

describe('ProposalFilter', () => {
  let proposals: ProposalListItem[]
  let proposalFilter: ProposalFilter

  describe('.filter', () => {
    beforeEach(() => {
      proposals = proposalData.map(convertProposalToIProposal)
      proposalFilter = new ProposalFilter(proposals)
    })

    it('should find all', () => {
      expect(proposalFilter.filterByText('')).toHaveLength(9)
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

const convertProposalToIProposal = (proposal: Proposal): ProposalListItem => {
  return {
    id: proposal.id,
    legacyId: proposal.legacyId,
    providerID: proposal.providerID,
    serviceType: proposal.serviceType,
    countryCode: proposal.countryCode,
    countryName: proposal.countryName,
    isFavorite: true,
    quality: null
  }
}
