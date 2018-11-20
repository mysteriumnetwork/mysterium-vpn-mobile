/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import * as React from 'react'

import { Modal, StyleSheet, View } from 'react-native'

type ModalProps = {
  isOpen: boolean,
  onClose: () => void
}

const CountryModal: React.SFC<ModalProps> = ({ isOpen, onClose, children }) => {
  return (
    <Modal
      animationType="slide"
      transparent={false}
      visible={isOpen}
      onRequestClose={() => onClose()}
    >
      <View style={styles.container}>
        {children}
      </View>
    </Modal>
  )
}

const styles: any = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%'
  }
})

export default CountryModal
