# calibration.xml editor 

## dev
### requirement
* node
* rust


### build - relase
* yarn postcss:release
* yarn shadow:release
* yarn tauri build

  * result
    * ./src-tauri/target/release/cljs-tauri.exe
    * ./src-tauri/target/release/bundle/msi/*.msi

## launch
### prequisite
* link: https://developer.microsoft.com/en-us/microsoft-edge/webview2/#download-section
* "Evergreen Standalone Installer"를 download. 

### release - 0.2.1
* change note
* calibration item 이름이 중복일 경우 처리하지 못하는 문제 
  * item 이름을 원복, X,Y,Z,R,P,Y -> X,Y,Z,Roll,Pitch,Yaw]
* window's title bar에 version 추가 

* windows-exe: http://10.90.180.72/HSS/xml-editor_view.two/-/wikis/uploads/f0f0f8a143d9141a2b99f481044f17d8/xml-editor.zip

### release - 0.2.0
* change note
  * 표기 되는 숫자 format 변경 - xyz.ab
  * calibration 항목은 한번만 출력 
  * file path를 file명만 출력
  * 화면 축소시 가로 scroll bar 추가 
  * toast, latest save time 제거 
  
* windows-exe: http://10.90.180.72/HSS/xml-editor_view.two/-/wikis/uploads/07df179e871fc9dfbb6cd8c5cef4951f/xml-editor.zip

### release - 0.1.0 (alpha)
* windows-msi: http://10.90.180.72/HSS/xml-editor_view.two/-/wikis/uploads/2e05c3afb8ab957e3622cdaf821be9bb/cljs-tauri_0.1.0_x64_en-US.msi
* windows-exe: http://10.90.180.72/HSS/xml-editor_view.two/-/wikis/uploads/f63d4f8d0364d71cb9d2a7bfd144c679/cljs-tauri.exe
