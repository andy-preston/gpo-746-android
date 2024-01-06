package andyp.gpo746.android

import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val ARBITRARY_REQUEST_CODE_READ_PHONE_STATE = 418

open class PermissionActivity : UselessActivity() {

    public override fun onStart() {
        super.onStart()
        checkPermission()
    }

    private fun checkPermission() {
        val permission = Manifest.permission.READ_PHONE_STATE
        val grantState = ContextCompat.checkSelfPermission(this, permission)
        val granted = grantState == PackageManager.PERMISSION_GRANTED
        permissionIndicator.setChecked(granted)
        if (granted) {
            logInfo("PermissionActivity", "Permission already granted")
        } else {
            logInfo("PermissionActivity", "Requesting permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                ARBITRARY_REQUEST_CODE_READ_PHONE_STATE
            )
        }
    }

    public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ARBITRARY_REQUEST_CODE_READ_PHONE_STATE) {
            val granted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            permissionIndicator.setChecked(granted)
            if (granted) {
                logInfo("PermissionActivity", "Granted")
            } else {
                logInfo("PermissionActivity", "Not granted")
            }
        }
    }
}
