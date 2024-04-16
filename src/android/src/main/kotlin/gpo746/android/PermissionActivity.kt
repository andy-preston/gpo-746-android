package andyp.gpo746.android

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val ARBITRARY_REQUEST_CODE = 418

abstract class PermissionActivity : UselessActivity() {

    private val permissionsRequired = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ANSWER_PHONE_CALLS
    )

    public override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    protected fun allAlreadyGranted(): Boolean =
        permissionsRequired.fold(true) { allGrantedSoFar, permission ->
            val grantState = ContextCompat.checkSelfPermission(this, permission)
            val granted = grantState == PackageManager.PERMISSION_GRANTED
            return if (allGrantedSoFar) granted else false
        }


    private fun checkPermissions() {
        val granted = allAlreadyGranted()
        permissionIndicator.setChecked(granted)
        if (granted) {
            logInfo("PermissionActivity", "Permissions already granted")
        } else {
            logInfo("PermissionActivity", "Requesting permissions")
            ActivityCompat.requestPermissions(
                this,
                permissionsRequired,
                ARBITRARY_REQUEST_CODE
            )
        }
    }

    public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ARBITRARY_REQUEST_CODE) {
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
