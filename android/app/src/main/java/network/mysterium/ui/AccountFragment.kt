/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

package network.mysterium.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.R

class AccountFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var accountMainLayout: ConstraintLayout
    private lateinit var accountIdentityRegistrationLayout: ConstraintLayout
    private lateinit var accountRegisterIdentityButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_account, container, false)

        toolbar = root.findViewById(R.id.account_toolbar)
        accountMainLayout = root.findViewById(R.id.account_main_layout)
        accountIdentityRegistrationLayout = root.findViewById(R.id.account_identity_registration_layout)
        accountRegisterIdentityButton = root.findViewById(R.id.account_register_identity_button)

        toolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.MAIN)
        }

        accountRegisterIdentityButton.setOnClickListener {
            accountIdentityRegistrationLayout.visibility = View.GONE
            accountMainLayout.visibility = View.VISIBLE
        }

        onBackPress {
            navigateTo(root, Screen.MAIN)
        }

        return root
    }
}
