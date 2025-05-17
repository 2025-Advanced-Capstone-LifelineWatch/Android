package com.example.lifeline.ui.mypage.detailed

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_terms)

        val tvTerms = findViewById<TextView>(R.id.tvTerm)

        tvTerms.text = """
[서비스 이용 약관]

제 1 조 (목적)
본 약관은 LifeLine 서비스(이하 "서비스")의 이용과 관련하여 회사와 이용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

제 2 조 (정의)
1. “회사”란 서비스를 운영하는 주체를 말합니다.
2. “이용자”란 본 약관에 따라 회사가 제공하는 서비스를 이용하는 자를 말합니다.

제 3 조 (약관의 효력 및 변경)
1. 본 약관은 서비스를 이용하고자 하는 모든 이용자에게 그 효력이 발생합니다.
2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 약관을 개정할 수 있습니다.

제 4 조 (서비스의 제공 및 변경)
1. 회사는 이용자에게 생체 데이터 기반의 건강 관리, 알람, 상담, 추천 기능 등을 제공합니다.
2. 회사는 서비스 운영상 또는 기술상 필요한 경우 제공 내용을 변경할 수 있습니다.

제 5 조 (서비스 이용의 제한)
회사는 다음 각 호의 경우 사전 통지 없이 서비스 이용을 제한하거나 중지할 수 있습니다:
1. 타인의 정보를 도용한 경우
2. 법령 또는 공공질서·미풍양속을 위반한 경우
3. 서비스의 정상적인 운영을 방해하는 행위가 있는 경우

제 6 조 (개인정보 보호)
1. 회사는 이용자의 개인정보를 보호하기 위해 노력하며, 관련 법령에 따라 개인정보처리방침을 수립하여 고지하고 준수합니다.
2. 회사는 서비스 운영에 필요한 최소한의 개인정보만을 수집하며, 이용자의 동의 없이 외부에 제공하지 않습니다.

제 7 조 (면책 조항)
회사는 다음 사유로 인해 발생한 손해에 대해 책임을 지지 않습니다:
1. 천재지변 또는 이에 준하는 불가항력으로 인한 경우
2. 이용자의 귀책사유로 인해 발생한 문제
3. 서비스의 일부 또는 전부의 장애

제 8 조 (분쟁 해결)
서비스 이용과 관련하여 분쟁이 발생한 경우 회사와 이용자는 성실히 협의하여 해결하며, 합의가 이루어지지 않을 경우 관할 법원에 소송을 제기할 수 있습니다.

※ 본 약관은 2025년 5월 1일부터 적용됩니다.
        """.trimIndent()
    }
}
