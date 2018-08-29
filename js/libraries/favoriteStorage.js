import { AsyncStorage } from 'react-native'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'
import Countries from '../libraries/countries'

const FAVORITE_KEY  = '@FavoritesStorage:Favorites'

class Storage {
  async getFavorites (): Map<string, boolean> {
    const values = await AsyncStorage.getItem(FAVORITE_KEY);
    if (values !== null) {
      return values
    }
  }

  async setFavorite (proposalId: string, isFavorite: boolean): void {
    var favorites = getFavorites()
    if (isFavorite) {
      favorites[proposalId] = isFavorite
    } else if (favorites[proposalId]) {
      delete favorites[proposalId]
    }
    await AsyncStorage.setItem(FAVORITE_KEY, favorites);
  }
}

const storage = new Storage()

class FavoriteProposalDTO {
  _proposal: ProposalDTO
  name: string
  id: string

  set isFavorite(newValue: boolean) {
    storage.setFavorite(this.id, newValue)
  }
  get isFavorite(): boolean {
    const favorites = storage.getFavorites()
    return favorites[this.id] === true
  }

  constructor (proposal: ProposalDTO) {
    super(proposal)
    const countryCode = p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    this.name = Countries[countryCode] || CONFIG.TEXTS.UNKNOWN
    this.id = `${proposal.providerId}_${proposal.id}`
  }

  compareTo (other: FavoriteProposalDTO): number {
    if (this.isFavorite && !other.isFavorite) {
      return -1
    } else if (!this.isFavorite && other.isFavorite) {
      return 1
    } else if (this.name > other.name) {
      return 1
    } else if (this.name < other.name) {
      return -1
    }
    return 0
  }

  static compare (a: FavoriteProposalDTO, b: FavoriteProposalDTO): number {
    return a.compareTo(b)
  }
}

function sortFavorites (proposals: ProposalDTO[]): FavoriteProposalDTO[] {
  return proposals
    .map(p => new FavoriteProposalDTO(p))
    .sort(FavoriteProposalDTO.compare)
}
