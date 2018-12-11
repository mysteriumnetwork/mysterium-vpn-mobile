import { reaction } from 'mobx'
import { IBugReporter } from '../bug-reporter/bug-reporter'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'

export default class Logger {
  constructor (private readonly tequilApiState: TequilApiState) {}

  public onIdentityUnlockSetUserIdInBugReporter (bugReporter: IBugReporter) {
    reaction(
      () => this.tequilApiState.identityId,
      (userId: string | undefined) => {
        if (!userId) return
        bugReporter.setUserId(userId)
      })
  }
}
