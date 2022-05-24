# calibration.xml editor 

## requirement
* node
* rust

## build - release
* yarn postcss:release
* yarn shadow:release
* yarn tauri build

  * result 
    * ./src-tauri/target/release/cljs-tauri.exe
    * ./src-tauri/target/release/bundle/msi/*.msi

### release - 0.2.1 
* change note
  * ...

### release - 0.2.0 (alpha)
* change note
  * 표기 되는 숫자 format 변경 - xyz.ab
  * calibration 항목은 한번만 출력 
  * file path를 file명만 출력
  * 화면 축소시 가로 scroll bar 추가 

### release - 0.1.0 (alpha)
