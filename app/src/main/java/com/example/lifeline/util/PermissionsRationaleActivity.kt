package com.example.lifeline.util

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.lifeline.R

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_rationale)

        val explanationText = findViewById<TextView>(R.id.tv_permission_reason)
        explanationText.text = """
            Lifeline 앱은 사용자의 건강 데이터를 기반으로 알람, 모니터링, 
            분석 서비스를 제공하기 위해 다음과 같은 권한이 필요합니다:

            - 심박수: 이상 감지 및 알림 제공
            - 혈압: 주기적 변화 모니터링
            - 걸음 수: 활동량 파악 및 분석

            이 데이터는 절대 외부로 전송되지 않으며, 기기 내에서만 처리됩니다.
        """.trimIndent()
    }
}
