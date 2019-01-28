import { Root as RootBase } from 'native-base'
import * as React from 'react'
import { onIdentityUnlockSetUserIdInBugReporter, setupGlobalErrorHandler } from '../bug-reporter/utils'
import App from './app'
import Container from './container'

class Root extends React.PureComponent {
  private readonly container = new Container()

  public async componentWillMount () {
    setupGlobalErrorHandler(this.container.bugReporter)

    onIdentityUnlockSetUserIdInBugReporter(this.container.identityManager, this.container.bugReporter)

    await this.container.favoritesStorage.fetch()
  }

  public render () {
    return (
      <RootBase>
        <App
          tequilAPIDriver={this.container.tequilAPIDriver}
          connectionStore={this.container.connectionStore}
          vpnScreenStore={this.container.vpnScreenStore}
          screenStore={this.container.screenStore}
          messageDisplayDelegate={this.container.messageDisplayDelegate}
          terms={this.container.terms}
          favorites={this.container.favorites}
          appLoader={this.container.appLoader}
          feedbackReporter={this.container.feedbackReporter}
        />
      </RootBase>
    )
  }
}

export default Root
