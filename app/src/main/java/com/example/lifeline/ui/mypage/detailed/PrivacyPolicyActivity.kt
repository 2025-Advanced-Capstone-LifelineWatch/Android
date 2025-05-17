package com.example.lifeline.ui.mypage.detailed

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_privacy_policy)

        val tvPrivacyPolicy = findViewById<TextView>(R.id.tvPrivacyPolicy)

        tvPrivacyPolicy.text = """
[개인정보 수집 및 이용 방침]

1. 수집하는 개인정보 항목
- 기본정보: 이름, 생년월일, 성별, 전화번호, 이메일 주소, 로그인 ID, 비밀번호
- 생체 데이터: 혈압, 체온, 호흡수, 산소 포화도, 걸음 수, 칼로리 소모량

2. 개인정보의 수집 및 이용 목적
- 회원 가입 및 본인 확인
- 생체 데이터를 활용한 건강 상태 모니터링 및 응급 알림 기능 제공
- 개인 맞춤형 건강 서비스 제공
- 고객 상담 및 공지사항 전달

3. 개인정보 보유 및 이용 기간
- 회원 탈퇴 시까지 보관 (단, 관련 법령에 따라 일정 기간 보존할 수 있음)

4. 개인정보 제3자 제공
- 아래의 목적에 한해 사용자 동의하에 외부 기관에 제공될 수 있음:
  · 응급 상황 시 사회복지사 및 보호자에게 생체 데이터 전송
  · 헬스케어 분석 및 통계 목적으로 비식별화된 데이터 제공
- 그 외에는 외부에 제공하지 않음

5. 이용자의 권리
- 언제든지 개인정보 열람, 수정, 삭제 요청 가능
- 생체 데이터 수집에 대한 동의 철회 가능
- 개인정보 관련 문의: support@example.com

※ 본 방침은 관련 법령 및 내부 정책에 따라 변경될 수 있으며, 변경 시 사전 공지됩니다.
        """.trimIndent()
    }
}
