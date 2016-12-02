;;
;; lizl 's mouse keys
;; 我有什么诉求需要使用 AutoHotKey 呢？
;; 有很多时候，我正高速打字中，需要临时用下鼠标（不记得快捷键或者没有快捷键），我就有一种特别的被打断的感觉。
;; 在家里和小灵子说话时，被打断我都很容易发火，更何况我真的在打字在思考？
;; 为了避免这种不被打断的感觉，我还入手了一把ThinkPad键盘。
;; 但是感觉并不好。因为，小红点对于击键来说，感觉也是不一样的，也有一种打断感。
;; 而且我会不由自主的去用小红点移动，而忘了使用快捷键。
;; 效率上其实降低了。
;; 为此，我决定使用 AHK ，模拟鼠标事件。我不用微软自带的，是因，它自定义在数字键区，而我的键盘是87键的 ~~

; define speeds

speeds := [1,8,64,256]

speeds_count := 4
speeds_index := 0

delta_1 := speeds%speeds_index%

switch_speed() 
{
	speeds_index := Mod(speeds_index+1, speeds_count)
	delta_1 := speeds%speeds_index%
}

; switch speed
capslock & o::
	switch_speed()
Return

; move quickly
capslock & j::
	MouseMove, -1*delta_1, 0, 20, R
Return

capslock & k::
	MouseMove, 0, delta_1, 20, R
Return

capslock & l::
	MouseMove, delta_1, 0, 20, R
Return

capslock & i::
	MouseMove, 0, -1*delta_1, 20, R
Return


; click
capslock & n::
	Click 
return

capslock & m::
	Click middle
return

capslock & <::
	Click right
return

capslock & y::
	Click down
return

capslock & u::
	MouseClick up
return