# calibration.xml editor 

## requirement
* node
* rust

## build - relase
* yarn postcss:release
* yarn shadow:release
* yarn tauri build

  * result
    * ./src-tauri/target/release/cljs-tauri.exe
    * ./src-tauri/target/release/bundle/msi/*.msi

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
