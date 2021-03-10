package updated.mysterium.vpn.ui.menu

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerArrayAdapter(
    context: Context,
    resource: Int,
    list: List<String>
) : ArrayAdapter<String>(context, resource, list) {

    var selectedPosition = 0

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return if (position == selectedPosition) {
            TextView(context).apply {
                visibility = View.GONE
                height = 0
            }
        } else {
            super.getDropDownView(position, null, parent)
        }
    }
}
