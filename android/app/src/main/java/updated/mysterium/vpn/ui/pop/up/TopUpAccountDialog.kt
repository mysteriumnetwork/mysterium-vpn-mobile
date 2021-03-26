package updated.mysterium.vpn.ui.pop.up

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding

class TopUpAccountDialog(context: Context) : AlertDialog(context) {

    private lateinit var binding: PopUpTopUpAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PopUpTopUpAccountBinding.inflate(layoutInflater)
        setView(binding.root)
    }

    inner class PopUpBuilder : AlertDialog.Builder(context)
}
