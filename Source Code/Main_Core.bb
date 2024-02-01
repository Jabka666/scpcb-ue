Include "Source Code\Math_Core.bb"
Include "Source Code\Strict_Functions_Core.bb"

Global ButtonSFX%
Global ButtonSFX2%

Global MenuWhite%, MenuGray%, MenuBlack%

Const TICK_DURATION# = 70.0 / 60.0

Type FramesPerSeconds
	Field Accumulator#
	Field PrevTime%
	Field CurrTime%
	Field FPS%
	Field TempFPS%
	Field Goal%
	Field LoopDelay%
	Field Factor#[2]
End Type

Global fps.FramesPerSeconds = New FramesPerSeconds

Global MilliSec%

fps\LoopDelay = MilliSecs()

Global SplitSpace$

If opt\LauncherEnabled
	Local lnchr.Launcher
	
	lnchr.Launcher = New Launcher
	
	lnchr\TotalGFXModes = CountGfxModes3D()
	
	opt\AspectRatio = 1.0
	
	UpdateLauncher(lnchr)
	
	Delete(lnchr)
EndIf

Global GraphicWidthFloat#, RealGraphicWidthFloat#
Global GraphicHeightFloat#, RealGraphicHeightFloat#

; ~ New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode -- Love Mark
If opt\DisplayMode = 1
	opt\RealGraphicWidth = DesktopWidth() : opt\RealGraphicHeight = DesktopHeight()
	GraphicWidthFloat = Float(opt\GraphicWidth) : GraphicHeightFloat = Float(opt\GraphicHeight)
	RealGraphicWidthFloat = Float(opt\RealGraphicWidth) : RealGraphicHeightFloat = Float(opt\RealGraphicHeight)
	opt\AspectRatio = (GraphicWidthFloat / GraphicHeightFloat) / (RealGraphicWidthFloat / RealGraphicHeightFloat)
	Graphics3DExt(opt\RealGraphicWidth, opt\RealGraphicHeight, 0, 4)
Else
	opt\AspectRatio = 1.0
	opt\RealGraphicWidth = opt\GraphicWidth : opt\RealGraphicHeight = opt\GraphicHeight
	GraphicWidthFloat = Float(opt\GraphicWidth) : GraphicHeightFloat = Float(opt\GraphicHeight)
	RealGraphicWidthFloat = Float(opt\RealGraphicWidth) : RealGraphicHeightFloat = Float(opt\RealGraphicHeight)
	Graphics3DExt(opt\GraphicWidth, opt\GraphicHeight, 0, (opt\DisplayMode = 2) + 1)
EndIf

AppTitle(Format(GetLocalString("misc", "title"), VersionNumber))

Global MenuScale# = opt\GraphicHeight / 1024.0

Global Input_ResetTime# = 0.0
Global MousePosX#, MousePosY#

Function UpdateMouseInput%()
	MousePosX = ScaledMouseX()
	MousePosY = ScaledMouseY()
	
	If Input_ResetTime > 0.0
		Input_ResetTime = Max(Input_ResetTime - fps\Factor[1], 0.0)
	Else
		mo\DoubleClick = False
		mo\MouseHit1 = MouseHit(1)
		If mo\MouseHit1
			If MilliSecs() - mo\LastMouseHit1 < 800 Then mo\DoubleClick = True
			mo\LastMouseHit1 = MilliSecs()
		EndIf
		
		Local PrevMouseDown1% = mo\MouseDown1
		
		mo\MouseDown1 = MouseDown(1)
		mo\MouseUp1 = (PrevMouseDown1 And (Not mo\MouseDown1))
		
		mo\MouseHit2 = MouseHit(2)
	EndIf
End Function

Function StopMouseMovement%()
	MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
	mo\Mouse_X_Speed_1 = 0.0
	mo\Mouse_Y_Speed_1 = 0.0
End Function

Function ResetInput%()
	FlushKeys()
	FlushMouse()
	mo\MouseHit1 = False
	mo\MouseHit2 = False
	mo\MouseDown1 = False
	mo\MouseUp1 = False
	mo\LastMouseHit1 = False
	GrabbedEntity = 0
	Input_ResetTime = 10.0
End Function

mo\Mouselook_X_Inc = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the X axis
mo\Mouselook_Y_Inc = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the Y axis
mo\Mouse_Left_Limit = 250 * MenuScale
mo\Mouse_Right_Limit = opt\GraphicWidth - mo\Mouse_Left_Limit
mo\Mouse_Top_Limit = 150 * MenuScale
mo\Mouse_Bottom_Limit = opt\GraphicHeight - mo\Mouse_Top_Limit ; ~ As above

; ~ Viewport
mo\Viewport_Center_X = opt\GraphicWidth / 2
mo\Viewport_Center_Y = opt\GraphicHeight / 2

SetBuffer(BackBuffer())

SeedRnd(MilliSec)

PlayStartupVideos()

Global CursorIMG%
If opt\DisplayMode = 0
	CursorIMG = LoadImage_Strict("GFX\Menu\cursor.png")
	CursorIMG = ScaleImage2(CursorIMG, MenuScale, MenuScale)
EndIf

InitLoadingScreens(LoadingScreensFile)

If (Not opt\PlayStartup) Then fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"))
fo\FontID[Font_Default_Big] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default_Big", "File"), GetFileLocalString(FontsFile, "Default_Big", "Size"))
fo\FontID[Font_Digital] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Digital", "File"), GetFileLocalString(FontsFile, "Digital", "Size"))
fo\FontID[Font_Digital_Big] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Digital_Big", "File"), GetFileLocalString(FontsFile, "Digital_Big", "Size"))
fo\FontID[Font_Journal] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Journal", "File"), GetFileLocalString(FontsFile, "Journal", "Size"))
fo\FontID[Font_Console] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Console", "File"), GetFileLocalString(FontsFile, "Console", "Size"))

SetFontEx(fo\FontID[Font_Default_Big])

Global BlinkMeterIMG% = LoadImage_Strict("GFX\HUD\blink_meter(1).png")
BlinkMeterIMG = ScaleImage2(BlinkMeterIMG, MenuScale, MenuScale)

RenderLoading(0, GetLocalString("loading", "core.main"))

RenderLoading(5, GetLocalString("loading", "core.achv"))

Include "Source Code\Achievements_Core.bb"

RenderLoading(10, GetLocalString("loading", "core.diff"))

Include "Source Code\Difficulty_Core.bb"

RenderLoading(15, GetLocalString("loading", "core.loading"))

Include "Source Code\Loading_Core.bb"

RenderLoading(20, GetLocalString("loading", "core.subtitle"))

Include "Source Code\Subtitles_Core.bb"

RenderLoading(25, GetLocalString("loading", "core.sound"))

Include "Source Code\Sounds_Core.bb"

RenderLoading(30, GetLocalString("loading", "core.item"))

Include "Source Code\Items_Core.bb"

RenderLoading(35, GetLocalString("loading", "core.particle"))

Include "Source Code\Particles_Core.bb"

RenderLoading(40, GetLocalString("loading", "core.grap"))

Include "Source Code\Graphics_Core.bb"

RenderLoading(45, GetLocalString("loading", "core.map"))

Include "Source Code\Map_Core.bb"

RenderLoading(65, GetLocalString("loading", "core.npc"))

Include "Source Code\NPCs_Core.bb"

RenderLoading(70, GetLocalString("loading", "core.event"))

Include "Source Code\Events_Core.bb"

RenderLoading(75, GetLocalString("loading", "core.save"))

Include "Source Code\Save_Core.bb"

RenderLoading(80, GetLocalString("loading", "core.menu"))

Include "Source Code\Menu_Core.bb"

InitMainMenuAssets()
MainMenuOpen = True
ResetInput()

RenderLoading(100)

InitErrorMsgs(12, True)
SetErrorMsg(0, Format(GetLocalString("error", "title"), VersionNumber))

SetErrorMsg(1, Format(Format(GetLocalString("error", "date"), CurrentDate(), "{0}"), CurrentTime(), "{1}"))
SetErrorMsg(2, Format(Format(Format(GetLocalString("error", "os"), SystemProperty("os"), "{0}"), (32 + (GetEnv("ProgramFiles(X86)") <> 0) * 32), "{1}"), SystemProperty("osbuild"), "{2}"))
SetErrorMsg(3, Format(Format(Format(GetLocalString("error", "cpu"), Trim(SystemProperty("cpuname")), "{0}"), SystemProperty("cpuarch"), "{1}"), GetEnv("NUMBER_OF_PROCESSORS"), "{2}"))

SetErrorMsg(10, Format(GetLocalString("error", "ex"), "_CaughtError_") + Chr(10))
SetErrorMsg(11, GetLocalString("error", "shot")) 

Function CatchErrors%(Location$)
	SetErrorMsg(9, Format(GetLocalString("error", "error"), Location))
End Function

Repeat
	SetErrorMsg(4, Format(Format(Format(GetLocalString("error", "gpu"), GfxDriverName(CountGfxDrivers()), "{0}"), ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)), "{1}"), (TotalVidMem() / 1024), "{2}"))
	SetErrorMsg(5, Format(Format(GetLocalString("error", "status"), ((TotalPhys() / 1024) - (AvailPhys() / 1024)), "{0}"), (TotalPhys() / 1024), "{1}"))
	
	Cls()
	
	Local ElapsedMilliSecs%
	
	MilliSec = MilliSecs()
	fps\CurrTime = MilliSec
	
	ElapsedMilliSecs = fps\CurrTime - fps\PrevTime
	If (ElapsedMilliSecs > 0 And ElapsedMilliSecs < 500) Then fps\Accumulator = fps\Accumulator + Max(0.0, Float(ElapsedMilliSecs) * 70.0 / 1000.0)
	fps\PrevTime = fps\CurrTime
	
	If opt\FrameLimit > 0.0
		Local WaitingTime% = (1000.0 / opt\FrameLimit) - (MilliSecs() - fps\LoopDelay)
		
		Delay(WaitingTime)
		fps\LoopDelay = MilliSecs()
	EndIf
	
	fps\Factor[0] = TICK_DURATION
	fps\Factor[1] = fps\Factor[0]
	
	If MainMenuOpen
		UpdateMainMenu()
	Else
		UpdateGame()
	EndIf
	
	RenderGamma()
	
	If KeyHit(key\SCREENSHOT) Then GetScreenshot()
	
	If opt\ShowFPS
		If fps\Goal < MilliSecs()
			fps\FPS = fps\TempFPS
			fps\TempFPS = 0
			fps\Goal = MilliSecs() + 1000
		Else
			fps\TempFPS = fps\TempFPS + 1
		EndIf
	EndIf
	
	Flip(opt\VSync)
Forever

Function UpdateGame%()
	Local e.Events, ev.Events, r.Rooms
	Local i%, TempStr$
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	
	If SelectedCustomMap = Null
		TempStr = GetLocalString("menu", "new.seed") + RandomSeed
	Else
		If Len(ConvertToUTF8(SelectedCustomMap\Name)) > 15
			TempStr = GetLocalString("menu", "new.map") + Left(ConvertToUTF8(SelectedCustomMap\Name), 14) + "..."
		Else
			TempStr = GetLocalString("menu", "new.map") + ConvertToUTF8(SelectedCustomMap\Name)
		EndIf
	EndIf
	SetErrorMsg(6, TempStr)
	SetErrorMsg(7, Format(GetLocalString("misc", "room"), RID))
	
	For ev.Events = Each Events
		If ev\room = PlayerRoom
			SetErrorMsg(8, Format(Format(Format(Format(Format(GetLocalString("misc", "event"), ev\EventID, "{0}"), ev\EventState, "{1}"), ev\EventState2, "{2}"), ev\EventState3, "{3}"), ev\EventState4, "{4}") + Chr(10))
			Exit
		EndIf
	Next
	
	CatchErrors("UpdateGame()")
	
	While fps\Accumulator > 0.0
		fps\Accumulator = fps\Accumulator - TICK_DURATION
		If fps\Accumulator <= 0.0 Then CaptureWorld()
		
		If MenuOpen Lor ConsoleOpen Then fps\Factor[0] = 0.0
		
		UpdateMouseInput()
		
		If (Not mo\MouseDown1) And (Not mo\MouseHit1) Then GrabbedEntity = 0
		
		If ShouldDeleteGadgets Then DeleteMenuGadgets()
		ShouldDeleteGadgets = False
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		
		UpdateStreamSounds()
		
		If (Not (MenuOpen Lor ConsoleOpen Lor me\EndingTimer < 0.0))
			DrawHandIcon = False
			For i = 0 To 2 Step 2
				DrawArrowIcon[i] = False
				DrawArrowIcon[i + 1] = False
			Next
			
			me\RestoreSanity = True
			ShouldEntitiesFall = True
			
			If PlayerInReachableRoom(False, True)
				ShouldPlay = Min(me\Zone, 2.0)
				
				If Rand(1500) = 1
					For i = 0 To 5
						If AmbientSFX(i, CurrAmbientSFX) <> 0
							If (Not ChannelPlaying(AmbientSFXCHN)) Then FreeSound_Strict(AmbientSFX(i, CurrAmbientSFX)) : AmbientSFX(i, CurrAmbientSFX) = 0
						EndIf
					Next
					
					PositionEntity(SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
					
					If Rand(3) = 1 Then me\Zone = 3
					
					If RID = r_cont1_173_intro
						me\Zone = 4
					ElseIf forest_event <> Null
						If PlayerRoom = forest_event\room
							If forest_event\EventState = 1.0
								me\Zone = 5
								PositionEntity(SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
							EndIf
						EndIf
					EndIf
					
					CurrAmbientSFX = Rand(0, AmbientSFXAmount[me\Zone] - 1)
					
					Select me\Zone
						Case 0, 1, 2
							;[Block]
							If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Zone" + (me\Zone + 1) + "\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
							;[End Block]
						Case 3
							;[Block]
							If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\General\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
							;[End Block]
						Case 4
							;[Block]
							If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Pre-breach\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
							;[End Block]
						Case 5
							;[Block]
							If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Forest\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
							;[End Block]
					End Select
					
					AmbientSFXCHN = PlaySound2(AmbientSFX(me\Zone, CurrAmbientSFX), Camera, SoundEmitter)
				EndIf
				UpdateSoundOrigin(AmbientSFXCHN, Camera, SoundEmitter)
				
				If Rand(50000) = 3
					If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) > 36.0 Then me\LightBlink = Rnd(1.0, 2.0)
					PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(8) + ".ogg"), True)
				EndIf
			EndIf
			
			me\SndVolume = CurveValue(0.0, me\SndVolume, 5.0)
			
			If (Not IsPlayerOutsideFacility()) Then HideDistance = 17.0
			UpdateDeaf()
			UpdateDecals()
			UpdateMouseLook()
			UpdateMoving()
			UpdateSaveState()
			UpdateVomit()
			UpdateEscapeTimer()
			InFacility = CheckForPlayerInFacility()
			DecalStep = 0
			If RID = r_dimension_1499
				If QuickLoadPercent > 0 And QuickLoadPercent < 100 Then ShouldEntitiesFall = False
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100 Then UpdateDimension1499()
				UpdateLeave1499()
			ElseIf RID = r_dimension_106
				LightVolume = 1.0
				UpdateSoundEmitters()
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100 Then UpdateDimension106()
			Else
				UpdateLights()
				UpdateEmitters()
				UpdateDoors()
				UpdateSecurityCams()
				UpdateScreens()
				UpdateSoundEmitters()
				If IsPlayerOutsideFacility()
					If QuickLoadPercent = -1 Lor QuickLoadPercent = 100 Then UpdateEndings()
				Else
					UpdateRooms()
					If QuickLoadPercent = -1 Lor QuickLoadPercent = 100 Then UpdateEvents()
				EndIf
				TimeCheckpointMonitors()
				UpdateMonitorSaving()
			EndIf
			UpdateZoneColor()
			UpdateMTF()
			UpdateNPCs()
			UpdateItems()
			UpdateParticles()
			Update268()
			Update427()
			
			If chs\InfiniteStamina Then me\Stamina = 100.0
			If chs\NoBlink Then me\BlinkTimer = me\BLINKFREQ
			
			me\BlurVolume = Min(CurveValue(0.0, me\BlurVolume, 20.0), 0.95)
			If me\BlurTimer > 0.0
				me\BlurVolume = Max(Min(0.95, me\BlurTimer / 1000.0), me\BlurVolume)
				me\BlurTimer = Max(me\BlurTimer - fps\Factor[0], 0.0)
			EndIf
			
			Local DarkAlpha# = 0.0
			
			If me\Sanity < 0.0
				If me\RestoreSanity Then me\Sanity = Min(me\Sanity + fps\Factor[0], 0.0)
				If me\Sanity < -200.0
					DarkAlpha = Max(Min((-me\Sanity - 200.0) / 700.0, 0.6), DarkAlpha)
					If (Not me\Terminated)
						me\HeartBeatVolume = Min(Abs(me\Sanity + 20.00) / 500.0, 1.0)
						me\HeartBeatRate = Max(70.0 + Abs(me\Sanity + 200.0) / 6.0, me\HeartBeatRate)
					EndIf
				EndIf
			EndIf
			
			If me\EyeStuck > 0.0
				me\BlinkTimer = me\BLINKFREQ
				me\EyeStuck = Max(me\EyeStuck - fps\Factor[0], 0.0)
				
				If me\EyeStuck < 9000.0 Then me\BlurTimer = Max(me\BlurTimer, (9000.0 - me\EyeStuck) * 0.5)
				If me\EyeStuck < 6000.0 Then DarkAlpha = Min(Max(DarkAlpha, (6000.0 - me\EyeStuck) / 5000.0), 1.0)
				If me\EyeStuck < 9000.0 And me\EyeStuck + fps\Factor[0] >= 9000.0 Then CreateMsg(GetLocalString("msg", "eyedrop.tear"))
			EndIf
			
			If me\BlinkTimer < 0.0
				If me\BlinkTimer > -5.0
					DarkAlpha = Max(DarkAlpha, Sin(Abs(me\BlinkTimer * 18.0)))
				ElseIf me\BlinkTimer > -15.0
					DarkAlpha = 1.0
				Else
					DarkAlpha = Max(DarkAlpha, Abs(Sin(me\BlinkTimer * 18.0)))
				EndIf
				
				If me\BlinkTimer <= -20.0
					; ~ Randomizes the frequency of blinking. Scales with difficulty
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							me\BLINKFREQ = Rnd(490.0, 700.0)
							;[End Block]
						Case NORMAL
							;[Block]
							me\BLINKFREQ = Rnd(455.0, 665.0)
							;[End Block]
						Case HARD
							;[Block]
							me\BLINKFREQ = Rnd(420.0, 630.0)
							;[End Block]
						Case EXTREME
							;[Block]
							me\BLINKFREQ = Rnd(200.0, 400.0)
							;[End Block]
					End Select
					me\BlinkTimer = me\BLINKFREQ
					If (Not (RID = r_room3_storage And EntityY(me\Collider) =< (-4100.0) * RoomScale)) Then me\BlurTimer = Max(me\BlurTimer - Rnd(50.0, 150.0), 0.0)
				EndIf
				me\BlinkTimer = me\BlinkTimer - fps\Factor[0]
			Else
				me\BlinkTimer = me\BlinkTimer - (fps\Factor[0] * 0.6 * me\BlinkEffect)
				If wi\NightVision = 0 And wi\SCRAMBLE = 0
					If me\EyeIrritation > 0.0 Then me\BlinkTimer = me\BlinkTimer - Min((me\EyeIrritation / 100.0) + 1.0, 4.0) * fps\Factor[0]
				EndIf
			EndIf
			
			me\EyeIrritation = Max(0.0, me\EyeIrritation - fps\Factor[0])
			
			If me\BlinkEffectTimer > 0.0
				me\BlinkEffectTimer = me\BlinkEffectTimer - (fps\Factor[0] / 70.0)
			Else
				me\BlinkEffect = 1.0
			EndIf
			
			me\LightBlink = Max(me\LightBlink - (fps\Factor[0] / 35.0), 0.0)
			If me\LightBlink > 0.0 And wi\NightVision = 0 Then DarkAlpha = Min(Max(DarkAlpha, me\LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If I_294\Using Then DarkAlpha = 1.0
			
			If wi\NightVision = 0 Then DarkAlpha = Max((1.0 - SecondaryLightOn) * 0.9, DarkAlpha)
			
			If me\Terminated
				NullSelectedStuff()
				me\BlurTimer = me\KillAnimTimer * 5.0
				If me\SelectedEnding <> -1
					MenuOpen = True
					me\EndingTimer = (-me\Terminated) * 0.1
				Else
					me\KillAnimTimer = me\KillAnimTimer + fps\Factor[0]
					If me\KillAnimTimer >= 400.0 Then MenuOpen = True
				EndIf
				DarkAlpha = Max(DarkAlpha, Min(Abs(me\Terminated / 400.0), 1.0))
			Else
				If (Not EntityHidden(t\OverlayID[9])) Then HideEntity(t\OverlayID[9])
				me\KillAnimTimer = 0.0
			EndIf
			
			If me\FallTimer < 0.0
				NullSelectedStuff()
				me\BlurTimer = Abs(me\FallTimer * 10.0)
				me\FallTimer = me\FallTimer - fps\Factor[0]
				DarkAlpha = Max(DarkAlpha, Min(Abs(me\FallTimer / 400.0), 1.0))
			EndIf
			
			If me\LightFlash > 0.0
				If EntityHidden(t\OverlayID[6]) Then ShowEntity(t\OverlayID[6])
				EntityAlpha(t\OverlayID[6], Max(Min(me\LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
				me\LightFlash = Max(me\LightFlash - (fps\Factor[0] / 70.0), 0.0)
			Else
				If (Not EntityHidden(t\OverlayID[6])) Then HideEntity(t\OverlayID[6])
			EndIf
			
			If (Not (SelectedItem = Null Lor InvOpen Lor OtherOpen <> Null))
				If IsItemInFocus() Then DarkAlpha = Max(DarkAlpha, 0.5)
			EndIf
			
			If SelectedScreen <> Null Lor d_I\SelectedDoor <> Null Then DarkAlpha = Max(DarkAlpha, 0.5)
			
			If DarkAlpha <> 0.0
				If EntityHidden(t\OverlayID[5]) Then ShowEntity(t\OverlayID[5])
				EntityAlpha(t\OverlayID[5], DarkAlpha)
			Else
				If (Not EntityHidden(t\OverlayID[5])) Then HideEntity(t\OverlayID[5])
			EndIf
		EndIf
		
		If fps\Factor[0] = 0.0
			UpdateWorld(0.0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
		EndIf
		
		UpdateWorld2()
		
		UpdateGUI()
		
		If KeyHit(key\INVENTORY)
			If d_I\SelectedDoor = Null And SelectedScreen = Null And (Not I_294\Using) And me\Playable And (Not me\Zombie) And me\VomitTimer >= 0.0 And me\FallTimer >= 0.0 And (Not me\Terminated) And me\SelectedEnding = -1
				If InvOpen
					StopMouseMovement()
				Else
					mo\DoubleClickSlot = -1
				EndIf
				InvOpen = (Not InvOpen)
				OtherOpen = Null
				SelectedItem = Null
			EndIf
		EndIf
		
		If KeyHit(key\SAVE)
			If SelectedDifficulty\SaveType < SAVE_ON_QUIT
				If CanSave = 0 ; ~ Scripted location
					CreateHintMsg(GetLocalString("save", "failed.now"))
				ElseIf CanSave = 1 ; ~ Endings / Intro location
					CreateHintMsg(GetLocalString("save", "failed.location"))
					If QuickLoadPercent > -1 Then CreateHintMsg(msg\HintTxt + GetLocalString("save", "failed.loading"))
				ElseIf CanSave = 2 ; ~ Triggered SCP-096
					CreateHintMsg(GetLocalString("save", "failed.096"))
				ElseIf as\Timer <= 70.0 * 5.0
					CancelAutoSave()
				ElseIf SelectedDifficulty\SaveType = SAVE_ON_SCREENS
					If SelectedScreen = Null And sc_I\SelectedMonitor = Null
						CreateHintMsg(GetLocalString("save", "failed.screen"))
					Else
						SaveGame(CurrSave\Name) ; ~ Can save at screen
					EndIf
				Else
					SaveGame(CurrSave\Name) ; ~ Can save
				EndIf
			Else
				CreateHintMsg(GetLocalString("save", "disable"))
			EndIf
		ElseIf SelectedDifficulty\SaveType = SAVE_ON_SCREENS And (SelectedScreen <> Null Lor sc_I\SelectedMonitor <> Null)
			If msg\HintTxt = "" Lor msg\HintTimer <= 0.0 Then CreateHintMsg(Format(GetLocalString("save", "save"), key\Name[key\SAVE]))
			If mo\MouseHit2 Then sc_I\SelectedMonitor = Null
		EndIf
		UpdateAutoSave()
		
		If KeyHit(key\CONSOLE)
			If opt\CanOpenConsole
				If ConsoleOpen
					UsedConsole = True
					ResumeSounds()
					StopMouseMovement()
					ShouldDeleteGadgets = True
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		UpdateMessages()
		UpdateHintMessages()
		UpdateSubtitles()
		
		UpdateConsole()
		
		UpdateQuickLoading()
		
		UpdateAchievementMsg()
		
		If me\EndingTimer < 0.0
			If me\SelectedEnding <> -1 Then UpdateEnding()
		Else
			If me\SelectedEnding = -1 Then UpdateMenu()
		EndIf
	Wend
	
	; ~ Go out of function immediately if the game has been quit
	If MainMenuOpen Then Return
	
	RenderGame()
	
	CatchErrors("Uncaught: UpdateGame()")
End Function

Global RenderTween#

Function RenderGame%()
	CatchErrors("RenderGame()")
	
	RenderTween = Max(0.0, 1.0 + (fps\Accumulator / TICK_DURATION))
	
	If fps\Factor[0] > 0.0 And PlayerInReachableRoom(False, True) Then RenderSecurityCams()
	
	RenderWorld2(RenderTween)
	
	If (Not (MenuOpen Lor InvOpen Lor ConsoleOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\EndingTimer < 0.0))
		RenderLights(Camera)
	EndIf
	
	RenderBlur(me\BlurVolume)
	
	RenderGUI()
	
	RenderMessages()
	RenderHintMessages()
	RenderSubtitles()
	
	RenderConsole()
	
	RenderQuickLoading()
	
	RenderAchievementMsg()
	
	If me\EndingTimer < 0.0
		If me\SelectedEnding <> -1 Then RenderEnding()
	Else
		If me\SelectedEnding = -1 Then RenderMenu()
	EndIf
	
	CatchErrors("Uncaught: RenderGame()")
End Function

Global WireFrameState%

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#, ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR%, ConsoleG%, ConsoleB%

Type ConsoleMsg
	Field Txt$
	Field IsCommand%
	Field R%, G%, B%
End Type

Function CreateConsoleMsg%(Txt$, R% = -1, G% = -1, B% = -1, IsCommand% = False)
	Local c.ConsoleMsg
	
	c.ConsoleMsg = New ConsoleMsg
	Insert c Before First ConsoleMsg
	
	c\Txt = Txt
	c\IsCommand = IsCommand
	
	c\R = R
	c\G = G
	c\B = B
	
	If c\R < 0 Then c\R = ConsoleR
	If c\G < 0 Then c\G = ConsoleG
	If c\B < 0 Then c\B = ConsoleB
End Function

Function CreateConsoleMultiMsg%(Txt$, R% = -1, G% = -1, B% = -1, IsCommand% = False)
	While Instr(Txt, "\n") <> 0 
		CreateConsoleMsg(Left(Txt, Instr(Txt, "\n") - 1), R, G, B, IsCommand)
		Txt = Right(Txt, Len(Txt) - Instr(Txt, "\n") - 1)
	Wend
	CreateConsoleMsg(Txt, R, G, B, IsCommand)
End Function

Type Cheats
	Field GodMode%
	Field NoBlink%
	Field NoTarget%
	Field NoClip%, NoClipSpeed#
	Field InfiniteStamina%
	Field SuperMan%, SuperManTimer#
	Field DebugHUD%
End Type

Global chs.Cheats

Function ClearCheats%()
	chs\GodMode = False
	chs\NoBlink = False
	chs\NoTarget = False
	chs\NoClip = False
	chs\InfiniteStamina = False
	chs\SuperMan = False
	chs\SuperManTimer = 0.0
	chs\DebugHUD = 0
End Function

Function InitCheats%()
	chs\GodMode = True
	chs\NoBlink = True
	chs\NoTarget = True
	chs\NoClip = True
	chs\InfiniteStamina = True
	chs\SuperMan = False
	chs\SuperManTimer = 0.0
	chs\DebugHUD = Rand(3)
End Function

Function ResetNegativeStats%(Revive% = False)
	Local e.Events
	Local i%
	
	me\Injuries = 0.0
	me\Bloodloss = 0.0
	
	me\BlurTimer = 0.0
	me\LightFlash = 0.0
	me\LightBlink = 0.0
	me\CameraShake = 0.0
	
	me\DeafTimer = 0.0
	
	me\DeathTimer = 0.0
	
	me\VomitTimer = 0.0
	me\HeartBeatVolume = 0.0
	
	If me\BlinkEffect > 1.0
		me\BlinkEffect = 1.0
		me\BlinkEffectTimer = 0.0
	EndIf
	
	If me\StaminaEffect > 1.0
		me\StaminaEffect = 1.0
		me\StaminaEffectTimer = 0.0
	EndIf
	me\Stamina = 100.0
	
	For i = 0 To 6
		I_1025\State[i] = 0.0
	Next
	
	If I_427\Timer >= 70.0 * 360.0 Then I_427\Timer = 0.0
	I_008\Timer = 0.0
	I_409\Timer = 0.0
	
	If Revive
		ClearCheats()
		
		; ~ If death by SCP-173 or SCP-106, enable GodMode, prevent instant death again -- Salvage
		If n_I\Curr173\Idle = 1
			CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-173"))
			chs\GodMode = True
			n_I\Curr173\Idle = 0
		EndIf
		If EntityDistanceSquared(me\Collider, n_I\Curr106\Collider) < 4.0
			CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-106"))
			chs\GodMode = True
		EndIf
		If n_I\Curr049 <> Null
			n_I\Curr049\State = 1.0 ; ~ Reset SCP-049
			If EntityDistanceSquared(me\Collider, n_I\Curr049\Collider) < 4.0
				CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-049"))
				chs\GodMode = True
			EndIf
		EndIf
		
		me\DropSpeed = -0.1
		me\HeadDropSpeed = 0.0
		me\Shake = 0.0
		me\CurrSpeed = 0.0
		
		me\FallTimer = 0.0
		MenuOpen = False
		
		HideEntity(me\Head)
		ShowEntity(me\Collider)
		
		me\Terminated = False
		me\KillAnim = 0
	EndIf
End Function

Function UpdateConsole%()
	CatchErrors("UpdateConsole()")
	
	If (Not opt\CanOpenConsole)
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen
		Local ev.Events, e.Events, e2.Events, r.Rooms, it.Items, n.NPCs, snd.Sound, cm.ConsoleMsg, itt.ItemTemplates
		Local Tex%, Tex2%, InBar%, InBox%, MouseScroll#, Temp%, i%
		Local Args$, StrTemp$, StrTemp2$, StrTemp3$, StrTemp4$
		Local x%, y%, Width%, Height%
		Local CoordEx% = 15 * MenuScale
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		x = 0
		y = opt\GraphicHeight - 300 * MenuScale
		Width = opt\GraphicWidth
		Height = 270 * MenuScale
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + CoordEx
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		If ScrollBarHeight > Height Then ScrollBarHeight = Height
		If ConsoleHeight < Height Then ConsoleHeight = Height
		
		InBar = MouseOn(x + Width - (26 * MenuScale), y, 26 * MenuScale, Height)
		InBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		If (Not mo\MouseDown1)
			ConsoleScrollDragging = False
		ElseIf ConsoleScrollDragging
			ConsoleScroll = ConsoleScroll + ((MousePosY - ConsoleMouseMem) * Height / ScrollBarHeight)
			ConsoleMouseMem = MousePosY
		EndIf
		
		If (Not ConsoleScrollDragging)
			If mo\MouseHit1
				If InBox
					ConsoleScrollDragging = True
					ConsoleMouseMem = MousePosY
				ElseIf InBar
					ConsoleScroll = ConsoleScroll + ((MousePosY - (y + Height)) * ConsoleHeight / Height + (Height / 2))
					ConsoleScroll = ConsoleScroll / 2
				EndIf
			EndIf
		EndIf
		
		MouseScroll = MouseZSpeed()
		If MouseScroll = 1
			ConsoleScroll = ConsoleScroll - CoordEx
		ElseIf MouseScroll= -1
			ConsoleScroll = ConsoleScroll + CoordEx
		EndIf
		
		Local ReissuePos%
		
		If KeyHit(200)
			ReissuePos = 0
			If ConsoleReissue = Null
				ConsoleReissue = First ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos - CoordEx
					ConsoleReissue = After ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos - CoordEx
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				ReissuePos = ReissuePos - CoordEx
				
				While True
					If ConsoleReissue = Null
						ConsoleReissue = First ConsoleMsg
						ReissuePos = 0
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos - CoordEx
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null
				ConsoleInput = ConsoleReissue\Txt
				ConsoleScroll = ReissuePos + (Height / 2)
			EndIf
		EndIf
		
		If KeyHit(208)
			ReissuePos = (-ConsoleHeight) + CoordEx
			If ConsoleReissue = Null
				ConsoleReissue = Last ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + CoordEx
					ConsoleReissue = Before ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos + CoordEx
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				ReissuePos = ReissuePos + CoordEx
				
				While True
					If ConsoleReissue = Null
						ConsoleReissue = Last ConsoleMsg
						ReissuePos = (-ConsoleHeight) + CoordEx
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + CoordEx
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null
				ConsoleInput = ConsoleReissue\Txt
				ConsoleScroll = ReissuePos + (Height / 2)
			EndIf
		EndIf
		
		If ConsoleScroll < (-ConsoleHeight) + Height Then ConsoleScroll = (-ConsoleHeight) + Height
		If ConsoleScroll > 0 Then ConsoleScroll = 0
		
		SelectedInputBox = 2
		
		Local OldConsoleInput$ = ConsoleInput
		
		ConsoleInput = UpdateMenuInputBox(x, y + Height, Width, 30 * MenuScale, ConsoleInput, Font_Console, 2)
		If OldConsoleInput <> ConsoleInput Then ConsoleReissue = Null
		ConsoleInput = Left(ConsoleInput, 100)
		
		If KeyHit(28) And ConsoleInput <> ""
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput, 255, 255, 0, True)
			If Instr(ConsoleInput, " ") <> 0
				StrTemp = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp = Lower(ConsoleInput)
			EndIf
			
			Select Lower(StrTemp)
				Case "help"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
					
					Select StrTemp
						Case "1", ""
							;[Block]
							CreateConsoleMsg(GetLocalString("console", "help_1.1"))
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("- noblink")
							CreateConsoleMsg("- asd")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- newyear")
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- weed")
							CreateConsoleMsg("- money")
							CreateConsoleMsg("- debughud")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg(GetLocalString("console", "help_1.2"))
							CreateConsoleMsg(GetLocalString("console", "help.command"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "2"
							;[Block]
							CreateConsoleMsg(GetLocalString("console", "help_2.1"))
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- reset372")
							CreateConsoleMsg("- 106retreat")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- disable049")
							CreateConsoleMsg("- enable049")
							CreateConsoleMsg("- disable966")
							CreateConsoleMsg("- enable966")
							CreateConsoleMsg("- doorcontrol")
							CreateConsoleMsg("- unlockcheckpoints")
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- disablenuke")
							CreateConsoleMsg("- resetfunds")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg(GetLocalString("console", "help_2.2"))
							CreateConsoleMsg(GetLocalString("console", "help.command"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "3"
							;[Block]
							CreateConsoleMsg(GetLocalString("console", "help_3.1"))
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- camerafog [near] [far]")
							CreateConsoleMsg("- spawn [npc type] [state]")
							CreateConsoleMsg("- injure [value]")
							CreateConsoleMsg("- infect [value]")
							CreateConsoleMsg("- crystal [value]")
							CreateConsoleMsg("- teleport [room name]")
							CreateConsoleMsg("- spawnitem [item name]")
							CreateConsoleMsg("- giveachievement [ID / All]")
							CreateConsoleMsg("- codes")
							CreateConsoleMsg("- loadzone [Zone ID]")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg(GetLocalString("console", "help.command"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "camerafog"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "camerafog"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.camerafog"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "noclip", "fly"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "noclip"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.noclip"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "noblink", "nb"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "noblink"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.noblink"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "godmode", "god"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "god"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.god"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "infinitestamina", "is"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "infinitestamina"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.is"))
							CreateConsoleMsg("******************************")
						Case "notarget", "nt"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "notarget"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.nt"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "wireframe", "wf"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "wireframe"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.wf"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "spawnitem", "si", "giveitem"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "spawnitem"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.si"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "spawn", "s"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "spawn"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.s"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "reset372" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "reset372"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.r372"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "106retreat" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "106retreat"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.106r"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable106"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable106"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.d106"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable106"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable106"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.e106"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable173"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable173"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.d173"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable173"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable173"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.e173"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "reset096" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "reset096"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.r096"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "doorcontrol" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "doorcontrol"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.dc"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "asd"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "asd"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.asd"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "unlockcheckpoints" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "unlockcheckpoints"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.uc"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable049"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable049"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.d049"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable049"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable049"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.e049"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable966"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable966"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.d966"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable966"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable966"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.e966"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "revive", "undead", "resurrect"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "revive"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.revive"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "teleport"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "teleport"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.teleport"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "stopsound", "stfu"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "stopsound"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.stfu"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "weed", "scp-420-j", "420j", "scp420-j", "scp-420j", "420"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "weed"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.weed"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "infect"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "infect"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.infect"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "crystal" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "crystal"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.crystal"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "resetfunds"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "resetfunds"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.rf"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "giveachievement"
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "giveachievement"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.ac"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "codes" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "codes"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.codes"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "loadzone" 
							;[Block]
							CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "loadzone"))
							CreateConsoleMsg("******************************")
							CreateConsoleMultiMsg(GetLocalString("console", "help.loadzone"))
							CreateConsoleMsg("******************************")
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg(GetLocalString("console", "help.no"), 255, 150, 0)
							;[End Block]
					End Select
					;[End Block]
				Case "ending"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "A"
							;[Block]
							me\SelectedEnding = Rand(Ending_A1, Ending_A2)
							;[End Block]
						Case "B"
							;[Block]
							me\SelectedEnding = Rand(Ending_B1, Ending_B2)
							;[End Block]
						Default
							;[Block]
							me\SelectedEnding = Rand(Ending_A1, Ending_B2)
							;[End Block]
					End Select
					
					me\Terminated = True
					;[End Block]
				Case "noclipspeed"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					chs\NoClipSpeed = Float(StrTemp)
					
					CreateConsoleMsg(Format(GetLocalString("console", "fly.speed"), StrTemp))
					;[End Block]
				Case "injure"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					me\Injuries = Float(StrTemp)
					
					CreateConsoleMsg(Format(GetLocalString("console", "inj"), StrTemp))
					;[End Block]
				Case "cls", "clear"
					;[Block]
					ClearConsole()
					;[End Block]
				Case "infect"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_008\Timer = Float(StrTemp)
					
					CreateConsoleMsg(Format(GetLocalString("console", "008"), StrTemp))
					;[End Block]
				Case "crystal"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_409\Timer = Float(StrTemp)
					
					CreateConsoleMsg(Format(GetLocalString("console", "409"), StrTemp))
					;[End Block]
				Case "heal"
					;[Block]
					ResetNegativeStats()
					CreateConsoleMsg(GetLocalString("console", "heal"))
					;[End Block]
				Case "teleport", "tp"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Temp = FindRoomID(StrTemp)
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\RoomID = Temp
							If r\RoomCenter <> 0
								TeleportEntity(me\Collider, EntityX(r\RoomCenter, True), EntityY(r\OBJ) + 0.5, EntityZ(r\RoomCenter, True), 0.3, True)
							Else
								TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.5, EntityZ(r\OBJ))
							EndIf
							TeleportToRoom(r)
							CreateConsoleMsg(Format(GetLocalString("console", "tp.success"), StrTemp))
							Exit
						EndIf
					Next
					
					If PlayerRoom\RoomTemplate\RoomID <> Temp Then CreateConsoleMsg(GetLocalString("console", "tp.failed"), 255, 0, 0)
					;[End Block]
				Case "spawnitem", "si", "giveitem", "gi"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Temp = False 
					For itt.ItemTemplates = Each ItemTemplates
						If Lower(itt\Name) = StrTemp
							Temp = True
							CreateConsoleMsg(Format(GetLocalString("console", "si.success"), itt\DisplayName))
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						ElseIf Lower(itt\DisplayName) = StrTemp
							Temp = True
							CreateConsoleMsg(Format(GetLocalString("console", "si.success"), itt\DisplayName))
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						ElseIf Lower(itt\TempName) = StrTemp
							Temp = True
							CreateConsoleMsg(Format(GetLocalString("console", "si.success"), itt\DisplayName))
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						EndIf
					Next
					
					If (Not Temp) Then CreateConsoleMsg(GetLocalString("console", "si.failed"), 255, 0, 0)
					;[End Block]
				Case "wireframe", "wf"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							WireFrameState = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							WireFrameState = False
							;[End Block]
						Default
							;[Block]
							WireFrameState = (Not WireFrameState)
							;[End Block]
					End Select
					
					If WireFrameState
						CreateConsoleMsg(GetLocalString("console", "wf.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "wf.off"))
					EndIf
					
					WireFrame(WireFrameState)
					;[End Block]
				Case "reset096", "r096"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType096
							n\State = 0.0
							StopStream_Strict(n\SoundCHN) : n\SoundCHN = 0 : n\SoundCHN_IsStream = False
							If n\SoundCHN2 <> 0 Then StopStream_Strict(n\SoundCHN2) : n\SoundCHN2 = 0 : n\SoundCHN2_IsStream = False
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "r096"))
					;[End Block]
				Case "reset372", "r372"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType372
							RemoveNPC(n)
							CreateEvent(e_cont1_372, r_cont1_372, 0, 0.0)
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "r372"))
					;[End Block]
				Case "disable173", "dis173"
					;[Block]
					n_I\Curr173\Idle = 3 ; ~ This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of SCP-173 state checks.
					HideEntity(n_I\Curr173\OBJ)
					HideEntity(n_I\Curr173\OBJ2)
					HideEntity(n_I\Curr173\Collider)
					CreateConsoleMsg(GetLocalString("console", "dis173"))
					;[End Block]
				Case "enable173", "en173"
					;[Block]
					n_I\Curr173\Idle = 0
					ShowEntity(n_I\Curr173\OBJ)
					ShowEntity(n_I\Curr173\OBJ2)
					ShowEntity(n_I\Curr173\Collider)
					CreateConsoleMsg(GetLocalString("console", "en173"))
					;[End Block]
				Case "disable106", "dis106"
					;[Block]
					n_I\Curr106\Idle = 1
					n_I\Curr106\State = 100000.0
					n_I\Curr106\Contained = True
					HideEntity(n_I\Curr106\Collider)
					HideEntity(n_I\Curr106\OBJ)
					HideEntity(n_I\Curr106\OBJ2)
					CreateConsoleMsg(GetLocalString("console", "dis106"))
					;[End Block]
				Case "enable106", "en106"
					;[Block]
					n_I\Curr106\Idle = 0
					n_I\Curr106\Contained = False
					ShowEntity(n_I\Curr106\Collider)
					ShowEntity(n_I\Curr106\OBJ)
					CreateConsoleMsg(GetLocalString("console", "en106"))
					;[End Block]
				Case "disable966", "dis966"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType966
							n\State = -1.0
							HideEntity(n\Collider)
							HideEntity(n\OBJ)
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "dis966"))
					;[End Block]
				Case "enable966", "en966"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType966
							n\State = 0.0
							ShowEntity(n\Collider)
							If wi\NightVision > 0 Then ShowEntity(n\OBJ)
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "en966"))
					;[End Block]
				Case "disable049", "dis049"
					;[Block]
					If n_I\Curr049 <> Null
						n_I\Curr049\Idle = 1
						HideEntity(n_I\Curr049\Collider)
						HideEntity(n_I\Curr049\OBJ)
					EndIf
					CreateConsoleMsg(GetLocalString("console", "dis049"))
					;[End Block]
				Case "enable049", "en049"
					;[Block]
					If n_I\Curr049 <> Null
						n_I\Curr049\Idle = 0
						ShowEntity(n_I\Curr049\Collider)
						ShowEntity(n_I\Curr049\OBJ)
					EndIf
					CreateConsoleMsg(GetLocalString("console", "en049"))
					;[End Block]
				Case "106retreat", "106r"
					;[Block]
					If n_I\Curr106\State <= 0.0
						n_I\Curr106\State = Rnd(22000.0, 27000.0)
						PositionEntity(n_I\Curr106\Collider, 0.0, 500.0, 0.0)
						ResetEntity(n_I\Curr106\Collider)
						CreateConsoleMsg(GetLocalString("console", "106r"))
					Else
						CreateConsoleMsg(GetLocalString("console", "106r.failed"), 255, 150, 0)
					EndIf
					;[End Block]
				Case "halloween"
					;[Block]
					n_I\IsHalloween = (Not n_I\IsHalloween)
					If n_I\IsHalloween
						Tex = LoadTexture_Strict("GFX\NPCs\scp_173_H.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex)
						EntityTexture(n_I\Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex)
						CreateConsoleMsg(GetLocalString("console", "halloween.on"))
					Else
						If n_I\IsNewYear Then n_I\IsNewYear = (Not n_I\IsNewYear)
						If n_I\IsAprilFools Then n_I\IsAprilFools = (Not n_I\IsAprilFools)
						Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex2, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex2)
						EntityTexture(n_I\Curr173\OBJ2, Tex2)
						DeleteSingleTextureEntryFromCache(Tex2)
						CreateConsoleMsg(GetLocalString("console", "halloween.off"))
					EndIf
					;[End Block]
				Case "newyear" 
					;[Block]
					n_I\IsNewYear = (Not n_I\IsNewYear)
					If n_I\IsNewYear
						Tex = LoadTexture_Strict("GFX\NPCs\scp_173_NY.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex)
						EntityTexture(n_I\Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex)
						CreateConsoleMsg(GetLocalString("console", "newyear.on"))
					Else
						If n_I\IsHalloween Then n_I\IsHalloween = (Not n_I\IsHalloween)
						If n_I\IsAprilFools Then n_I\IsAprilFools = (Not n_I\IsAprilFools)
						Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex2, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex2)
						EntityTexture(n_I\Curr173\OBJ2, Tex2)
						DeleteSingleTextureEntryFromCache(Tex2)
						CreateConsoleMsg(GetLocalString("console", "newyear.off"))
					EndIf
					;[End Block]
				Case "joke" 
					;[Block]
					n_I\IsAprilFools = (Not n_I\IsAprilFools)
					If n_I\IsAprilFools
						Tex = LoadTexture_Strict("GFX\NPCs\scp_173_J.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex)
						EntityTexture(n_I\Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex)
						CreateConsoleMsg(GetLocalString("console", "aprilfools.on"))
					Else
						If n_I\IsHalloween Then n_I\IsHalloween = (Not n_I\IsHalloween)
						If n_I\IsNewYear Then n_I\IsNewYear = (Not n_I\IsNewYear)
						Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png", 1)
						If opt\Atmosphere Then TextureBlend(Tex2, 5)
						EntityTexture(n_I\Curr173\OBJ, Tex2)
						EntityTexture(n_I\Curr173\OBJ2, Tex2)
						DeleteSingleTextureEntryFromCache(Tex2)
						CreateConsoleMsg(GetLocalString("console", "aprilfools.off"))
					EndIf
					;[End Block]
				Case "sanic"
					;[Block]
					chs\SuperMan = (Not chs\SuperMan)
					If chs\SuperMan
						CreateConsoleMsg(GetLocalString("console", "sanic.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "sanic.off"))
					EndIf
					;[End Block]
				Case "scp-420-j", "420", "weed", "scp420-j", "scp-420j", "420j"
					;[Block]
					For i = 1 To 20
						If Rand(2) = 1
							StrTemp = "Some SCP-420-J"
							StrTemp2 = "scp420j"
						Else
							StrTemp = "Joint"
							StrTemp2 = "joint"
						EndIf
						it.Items = CreateItem(StrTemp, StrTemp2, EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						EntityType(it\Collider, HIT_ITEM)
					Next
					PlaySound_Strict(LoadTempSound("SFX\Music\Using420J.ogg"))
					;[End Block]
				Case "godmode", "god"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\GodMode = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\GodMode = False
							;[End Block]
						Default
							;[Block]
							chs\GodMode = (Not chs\GodMode)
							;[End Block]
					End Select
					If chs\GodMode
						CreateConsoleMsg(GetLocalString("console", "god.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "god.off"))
					EndIf
					;[End Block]
				Case "revive", "undead", "resurrect"
					;[Block]
					ResetNegativeStats(True)
					;[End Block]
				Case "noclip", "fly"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoClip = True
							me\Playable = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoClip = False
							RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							;[End Block]
						Default
							;[Block]
							chs\NoClip = (Not chs\NoClip)
							If (Not chs\NoClip)
								RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							Else
								me\Playable = True
							EndIf
							;[End Block]
					End Select
					
					If chs\NoClip
						CreateConsoleMsg(GetLocalString("console", "fly.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "fly.off"))
					EndIf
					
					me\DropSpeed = 0.0
					;[End Block]
				Case "noblink", "nb"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoBlink = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoBlink = False
							;[End Block]
						Default
							;[Block]
							chs\NoBlink = (Not chs\NoBlink)
							;[End Block]
					End Select
					If chs\NoBlink
						CreateConsoleMsg(GetLocalString("console", "nb.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "nb.off"))
					EndIf
					;[End Block]
				Case "debughud", "dbh"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					Select StrTemp
						Case "game", "1"
							;[Block]
							chs\DebugHUD = 1
							;[End Block]
						Case "player", "me", "2"
							;[Block]
							chs\DebugHUD = 2
							;[End Block]
						Case "scps", "scp", "3"
							;[Block]
							chs\DebugHUD = 3
							;[End Block]
						Case "off", "false", "0"
							;[Block]
							chs\DebugHUD = 0
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg(GetLocalString("console", "debug.cate"), 255, 150, 0)
							;[End Block]
					End Select
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					KillSounds()
					
					For e.Events = Each Events
						If e\EventID = e_cont1_173
							For i = 0 To 2
								If e\room\NPC[i] <> Null Then RemoveNPC(e\room\NPC[i])
								If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
							Next
							If n_I\Curr173\Idle = 1 Then n_I\Curr173\Idle = 0
							PositionEntity(n_I\Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(n_I\Curr173\Collider)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "stfu"))
					;[End Block]
				Case "camerafog", "cf"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					If opt\DebugMode = 1
						opt\CameraFogFar = StrTemp
					Else
						opt\CameraFogFar = Clamp(StrTemp, 6.0, 17.0)
					EndIf
					CreateConsoleMsg(Format(GetLocalString("console", "fog"), opt\CameraFogFar, "{0}"))
					;[End Block]
				Case "spawn", "s"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1)
					StrTemp2 = Piece(Args, 2)
					
					; ~ Hacky fix for when the user doesn't input a second parameter.
					If StrTemp <> StrTemp2
						ConsoleSpawnNPC(StrTemp, StrTemp2)
					Else
						ConsoleSpawnNPC(StrTemp)
					EndIf
					;[End Block]
				Case "infinitestamina", "infstam", "is"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\InfiniteStamina = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\InfiniteStamina = False
							;[End Block]
						Default
							;[Block]
							chs\InfiniteStamina = (Not chs\InfiniteStamina)
							;[End Block]
					End Select
					
					If chs\InfiniteStamina
						CreateConsoleMsg(GetLocalString("console", "is.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "is.off"))
					EndIf
					;[End Block]
				Case "money", "rich"
					;[Block]
					For i = 1 To 20
						If Rand(2) = 1
							StrTemp = "Quarter"
							StrTemp2 = "25ct"
						Else
							StrTemp = "Coin"
							StrTemp2 = "coin"
						EndIf
						it.Items = CreateItem(StrTemp, StrTemp2, EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						EntityType(it\Collider, HIT_ITEM)
					Next
					;[End Block]
				Case "doorcontrol"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							RemoteDoorOn = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							RemoteDoorOn = False
							;[End Block]
						Default
							;[Block]
							RemoteDoorOn = (Not RemoteDoorOn)
							;[End Block]
					End Select
					
					If RemoteDoorOn
						CreateConsoleMsg(GetLocalString("console", "door.on"))
					Else
						CreateConsoleMsg(GetLocalString("console", "door.off"))
					EndIf
					
					For e2.Events = Each Events
						If e2\EventID = e_room2c_ec
							UpdateLever(e2\room\RoomLevers[2]\OBJ)
							RotateEntity(e2\room\RoomLevers[2]\OBJ, -80.0 + (160.0 * RemoteDoorOn), EntityYaw(e2\room\RoomLevers[2]\OBJ), 0.0)
							Exit
						EndIf
					Next
					;[End Block]
				Case "unlockcheckpoints"
					;[Block]
					For e2.Events = Each Events
						If e2\EventID = e_room2_sl
							e2\EventState3 = 0.0
							UpdateLever(e2\room\RoomLevers[0]\OBJ)
							RotateEntity(e2\room\RoomLevers[0]\OBJ, -80.0, EntityYaw(e2\room\RoomLevers[0]\OBJ), 0.0)
						ElseIf e2\EventID = e_cont2_008
							e2\EventState = 2.0
							UpdateLever(e2\room\Objects[1])
							RotateEntity(e2\room\Objects[1], -80.0, EntityYaw(e2\room\Objects[1]), 30.0)
						EndIf
					Next
					
					CreateConsoleMsg(GetLocalString("console", "uc"))
					;[End Block]
				Case "disablenuke"
					;[Block]
					For e2.Events = Each Events
						If e2\EventID = e_room2_nuke
							e2\EventState = 0.0
							UpdateLever(e2\room\RoomLevers[0]\OBJ)
							UpdateLever(e2\room\RoomLevers[1]\OBJ)
							RotateEntity(e2\room\RoomLevers[0]\OBJ, -80.0, EntityYaw(e2\room\RoomLevers[0]\OBJ), 0.0)
							RotateEntity(e2\room\RoomLevers[1]\OBJ, -80.0, EntityYaw(e2\room\RoomLevers[1]\OBJ), 0.0)
							Exit
						EndIf
					Next
					;[End Block]
				Case "unlockexits"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "a"
							;[Block]
							For e.Events = Each Events
								If e\EventID = e_gate_a_entrance
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg(GetLocalString("console", "ue.a"))
							;[End Block]
						Case "b"
							;[Block]
							For e.Events = Each Events
								If e\EventID = e_gate_b_entrance
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg(GetLocalString("console", "ue.b"))
							;[End Block]
						Default
							;[Block]
							For e.Events = Each Events
								If e\EventID = e_gate_b_entrance Lor e\EventID = e_gate_a_entrance
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
								EndIf
							Next
							CreateConsoleMsg(GetLocalString("console", "ue"))
							;[End Block]
					End Select
					RemoteDoorOn = True
					;[End Block]
				Case "kill", "suicide"
					;[Block]
					me\Terminated = True
					Select Rand(4)
						Case 1
							;[Block]
							msg\DeathMsg = GetLocalString("death", "kill_1")
							;[End Block]
						Case 2
							;[Block]
							msg\DeathMsg = Format(GetLocalString("death", "kill_2"), SubjectName)
							;[End Block]
						Case 3
							;[Block]
							msg\DeathMsg = GetLocalString("death", "kill_3")
							;[End Block]
						Case 4
							;[Block]
							msg\DeathMsg = Format(GetLocalString("death", "kill_4"), SubjectName)
							;[End Block]
					End Select
					;[End Block]
				Case "tele"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1, " ")
					StrTemp2 = Piece(Args, 2, " ")
					StrTemp3 = Piece(Args, 3, " ")
					PositionEntity(me\Collider, Float(StrTemp), Float(StrTemp2), Float(StrTemp3))
					PositionEntity(Camera, Float(StrTemp), Float(StrTemp2), Float(StrTemp3))
					ResetEntity(me\Collider)
					ResetEntity(Camera)
					CreateConsoleMsg(Format(Format(Format(GetLocalString("console", "tele"), EntityX(me\Collider), "{0}"), EntityY(me\Collider), "{1}"), EntityZ(me\Collider), "{2}"))
					;[End Block]
				Case "asd"
					;[Block]
					chs\NoBlink = True
					chs\NoTarget = True
					chs\NoClip = True
					chs\GodMode = True
					chs\InfiniteStamina = True
					
					opt\CameraFogFar = 17.0
					
					KillSounds()
					
					For e.Events = Each Events
						If e\EventID = e_cont1_173
							For i = 0 To 2
								If e\room\NPC[i] <> Null Then RemoveNPC(e\room\NPC[i])
								If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
							Next
							If n_I\Curr173\Idle = 1 Then n_I\Curr173\Idle = 0
							PositionEntity(n_I\Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(n_I\Curr173\Collider)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "stfu"))
					;[End Block]
				Case "notarget", "nt"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoTarget = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoTarget = False
							;[End Block]
						Default
							;[Block]
							chs\NoTarget = (Not chs\NoTarget)
							;[End Block]
					End Select
					
					If (Not chs\NoTarget)
						CreateConsoleMsg(GetLocalString("console", "nt.off"))
					Else
						CreateConsoleMsg(GetLocalString("console", "nt.on"))
					EndIf
					;[End Block]
				Case "spawnpumpkin", "pumpkin"
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "pumpkin"))
					;[End Block]
				Case "teleport173"
					;[Block]
					PositionEntity(n_I\Curr173\Collider, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
					ResetEntity(n_I\Curr173\Collider)
					;[End Block]
				Case "seteventstate"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1, " ")
					StrTemp2 = Piece(Args, 2, " ")
					StrTemp3 = Piece(Args, 3, " ")
					StrTemp4 = Piece(Args, 4, " ")
					
					Local PL_Room_Found% = False
					
					If StrTemp = "" Lor StrTemp2 = "" Lor StrTemp3 = "" Lor StrTemp4 = ""
						CreateConsoleMsg(GetLocalString("console", "ses.failed"), 255, 150, 0)
					Else
						For e.Events = Each Events
							If PlayerRoom = e\room
								If Lower(StrTemp) <> "keep" Then e\EventState = Float(StrTemp)
								If Lower(StrTemp2) <> "keep" Then e\EventState2 = Float(StrTemp2)
								If Lower(StrTemp3) <> "keep" Then e\EventState3 = Float(StrTemp3)
								If Lower(StrTemp4) <> "keep" Then e\EventState4 = Float(StrTemp4)
								CreateConsoleMsg(Format(Format(Format(Format(GetLocalString("console", "ses.success"), e\EventState, "{0}"), e\EventState2, "{1}"), e\EventState3, "{2}"), e\EventState4, "{3}"))
								PL_Room_Found = True
								Exit
							EndIf
						Next
						If (Not PL_Room_Found) Then CreateConsoleMsg(GetLocalString("console", "ses.failed.apply"), 255, 150, 0)
					EndIf
					;[End Block]
				Case "giveachievement"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					If StrTemp = "all"
						For i = 0 To MaxAchievements - 2 Step 2
							achv\Achievement[i] = True
							achv\Achievement[i + 1] = True
						Next
						CreateConsoleMsg(GetLocalString("console", "ga.all"))
					EndIf
					
					If Int(StrTemp) >= 0 And Int(StrTemp) < MaxAchievements And StrTemp <> "all"
						achv\Achievement[Int(StrTemp)] = True
						CreateConsoleMsg(Format(GetLocalString("console", "ga.success"), achv\AchievementStrings[Int(StrTemp)]))
					ElseIf StrTemp <> "all"
						CreateConsoleMsg(Format(GetLocalString("console", "ga.failed"), Int(StrTemp)), 255, 0, 0)
					EndIf
					;[End Block]
				Case "427state"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = 70.0 * Float(StrTemp)
					;[End Block]
				Case "teleport106"
					;[Block]
					n_I\Curr106\State = 0.0
					n_I\Curr106\Idle = 0
					;[End Block]
				Case "jorge"
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "jorge"))
					;[End Block]
				Case "resetfunds"
					;[Block]
					me\Funds = Rand(6)
					CreateConsoleMsg(GetLocalString("console", "funds"))
					;[End Block]
				Case "codes"
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "codes_1"))
					CreateConsoleMsg("")
					CreateConsoleMsg(Format(GetLocalString("console", "codes_2"), CODE_DR_MAYNARD))
					CreateConsoleMsg(Format(GetLocalString("console", "codes_3"), CODE_DR_HARP))
					CreateConsoleMsg(Format(GetLocalString("console", "codes_4"), CODE_DR_L))
					CreateConsoleMsg(Format(GetLocalString("console", "codes_5"), CODE_O5_COUNCIL))
					CreateConsoleMsg(Format(GetLocalString("console", "codes_6"), CODE_MAINTENANCE_TUNNELS))
					CreateConsoleMsg(Format(GetLocalString("console", "codes_7"), CODE_CONT1_035))
					CreateConsoleMsg("")
					CreateConsoleMsg(GetLocalString("console", "codes_8"))
					;[End Block]
				Case "loadzone", "lz"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					If Int(StrTemp) > 0 And Int(StrTemp) < MaxZones
						If Int(StrTemp) = CurrentZone
							CreateConsoleMsg(Format(GetLocalString("console", "loadzone_1"), Int(StrTemp)), 255, 0, 0)
						Else
							LoadZone(Int(StrTemp), False)
						EndIf
					Else
						CreateConsoleMsg(Format(GetLocalString("console", "loadzone_2"), Int(StrTemp)), 255, 0, 0)
					EndIf
					;[End Block]
				Default
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "notfound"), 255, 0, 0)
					;[End Block]
			End Select
			ConsoleInput = ""
		EndIf
		
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000 Then Delete(cm)
		Next
	EndIf
	
	CatchErrors("Uncaught: UpdateConsole()")
End Function

Function RenderConsole%()
	If (Not opt\CanOpenConsole) Then Return
	
	CatchErrors("RenderConsole()")
	
	If ConsoleOpen
		Local cm.ConsoleMsg
		Local InBar%, InBox%
		Local x%, y%, Width%, Height%
		Local TempStr$
		Local CoordEx% = 26 * MenuScale
		
		SetFontEx(fo\FontID[Font_Console])
		
		x = 0
		y = opt\GraphicHeight - 300 * MenuScale
		Width = opt\GraphicWidth
		Height = 270 * MenuScale
		
		RenderFrame(x, y, Width, Height + (30 * MenuScale))
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + (15 * MenuScale)
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		If ScrollBarHeight > Height Then ScrollBarHeight = Height
		If ConsoleHeight < Height Then ConsoleHeight = Height
		
		Color(50, 50, 50)
		InBar = MouseOn(x + Width - CoordEx, y, CoordEx, Height)
		If InBar Then Color(70, 70, 70)
		Rect(x + Width - CoordEx, y, CoordEx, Height)
		
		Color(120, 120, 120)
		InBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		If InBox Then Color(200, 200, 200)
		If ConsoleScrollDragging Then Color(255, 255, 255)
		Rect(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		Color(255, 255, 255)
		
		Local TempY# = y + Height - (25.0 * MenuScale) - ConsoleScroll
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000
				Delete(cm)
			Else
				If TempY >= y And TempY < y + Height - (20 * MenuScale)
					If cm = ConsoleReissue
						Color(cm\R / 4, cm\G / 4, cm\B / 4)
						Rect(x, TempY - (2 * MenuScale), Width - (30 * MenuScale), 24 * MenuScale, True)
					EndIf
					Color(cm\R, cm\G, cm\B)
					If cm\IsCommand
						TempStr = "> " + cm\Txt
					Else
						TempStr = cm\Txt
					EndIf
					TextEx(x + (20 * MenuScale), TempY, TempStr)
				EndIf
				TempY = TempY - (15.0 * MenuScale)
			EndIf
		Next
		Color(255, 255, 255)
		
		RenderMenuInputBoxes()
		
		RenderCursor()
	EndIf
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderConsole()")
End Function

Function ClearConsole%()
	Local c.ConsoleMsg
	
	For c.ConsoleMsg = Each ConsoleMsg
		Delete(c)
	Next
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("")
	CreateConsoleMsg("Console commands: ")
	CreateConsoleMsg(" - help [page]")
	CreateConsoleMsg(" - teleport [room name]")
	CreateConsoleMsg(" - godmode [on / off]")
	CreateConsoleMsg(" - noclip [on / off]")
	CreateConsoleMsg(" - infinitestamina [on / off]")
	CreateConsoleMsg(" - noblink [on / off]")
	CreateConsoleMsg(" - notarget [on / off]")
	CreateConsoleMsg(" - noclipspeed [x] (default = 2.0)")
	CreateConsoleMsg(" - wireframe [on / off]")
	CreateConsoleMsg(" - debughud [category]")
	CreateConsoleMsg(" - camerafog [near] [far]")
	CreateConsoleMsg(" - heal")
	CreateConsoleMsg(" - revive")
	CreateConsoleMsg(" - asd")
	CreateConsoleMsg(" - spawnitem [item name]")
	CreateConsoleMsg(" - 106retreat")
	CreateConsoleMsg(" - disable173 / enable173")
	CreateConsoleMsg(" - disable106 / enable106")
	CreateConsoleMsg(" - spawn [NPC type]")
End Function

Function OpenConsoleOnError%(ConsoleMsg$)
	If MenuOpen Lor ConsoleOpen Lor (Not opt\ConsoleOpening) Lor (Not opt\CanOpenConsole) Then Return
	If (MilliSec Mod 1500) < 800
		If ConsoleMsg <> "" Then CreateConsoleMsg(ConsoleMsg)
		ConsoleOpen = True
	EndIf
End Function

Type Messages
	Field Txt$
	Field Timer#
	Field DeathMsg$
	Field KeyPadMsg$
	Field KeyPadTimer#
	Field KeyPadInput$
	Field HintTxt$
	Field HintTimer#
	Field HintY#
End Type

Global msg.Messages

Function CreateMsg%(Txt$, Sec# = 6.0)
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	msg\Txt = Txt
	msg\Timer = 70.0 * Sec
End Function

Function UpdateMessages%()
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	If msg\Timer > 0.0
		msg\Timer = msg\Timer - fps\Factor[0]
	Else
		msg\Timer = 0.0 : msg\Txt = ""
	EndIf
End Function

Function RenderMessages%()
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	If msg\Timer > 0.0
		Local Temp%
		
		If (Not (InvOpen Lor OtherOpen <> Null)) Then Temp = ((I_294\Using Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null) Lor (SelectedItem <> Null And (SelectedItem\ItemTemplate\TempName = "paper" Lor SelectedItem\ItemTemplate\TempName = "oldpaper")))
		
		Local Temp2% = Min(msg\Timer / 2.0, 255.0)
		
		SetFontEx(fo\FontID[Font_Default])
		If (Not Temp)
			Color(Temp2, Temp2, Temp2)
			TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y + (200 * MenuScale), msg\Txt, True)
		Else
			Color(Temp2, Temp2, Temp2)
			TextEx(mo\Viewport_Center_X, opt\GraphicHeight * 0.94, msg\Txt, True)
		EndIf
	EndIf
	Color(255, 255, 255)
	If opt\ShowFPS
		Local CoordEx% = 20 * MenuScale
		
		SetFontEx(fo\FontID[Font_Console])
		TextEx(CoordEx, CoordEx, "FPS: " + fps\FPS)
		SetFontEx(fo\FontID[Font_Default])
	EndIf
End Function

Function CreateHintMsg%(Txt$, Sec# = 6.0)
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	msg\HintTxt = Txt
	msg\HintTimer = 70.0 * Sec
End Function

Function UpdateHintMessages%()
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width = StringWidth(msg\HintTxt) + (20 * Scale)
	Local Height% = 30 * Scale
	
	If msg\HintTxt <> ""
		If msg\HintTimer > 0.0
			If msg\HintY < Height
				msg\HintY = Min(msg\HintY + (2.0 * fps\Factor[0]), Height)
			Else
				msg\HintY = Height
			EndIf
			msg\HintTimer = msg\HintTimer - fps\Factor[0]
		Else
			If msg\HintY > 0.0
				msg\HintY = Max(msg\HintY - (2.0 * fps\Factor[0]), 0.0)
			Else
				msg\HintTxt = ""
				msg\HintTimer = 0.0
				msg\HintY = 0.0
			EndIf
		EndIf
	EndIf
End Function

Function RenderHintMessages%()
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width% = StringWidth(msg\HintTxt) + (20 * Scale)
	Local Height% = 30 * Scale
	Local x% = mo\Viewport_Center_X - (Width / 2)
	Local y% = (-Height) + msg\HintY
	
	If msg\HintTxt <> ""
		RenderFrame(x, y, Width, Height)
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(mo\Viewport_Center_X, y + (Height / 2), msg\HintTxt, True, True)
	EndIf
End Function

Function Kill%(IsBloody% = False)
	If chs\GodMode Then Return
	
	Local de.Decals
	
	StopBreathSound()
	
	If (Not me\Terminated)
		If IsBloody
			If EntityHidden(t\OverlayID[9]) Then ShowEntity(t\OverlayID[9])
			de.Decals = CreateDecal(DECAL_BLOOD_6, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, 0.1)
			de\SizeChange = 0.0025
			EntityParent(de\OBJ, PlayerRoom\OBJ)
		EndIf
		
		me\KillAnim = Rand(0, 1) : me\ForceMove = 0.0
		PlaySound_Strict(DamageSFX[0])
		If SelectedDifficulty\SaveType => SAVE_ON_QUIT
			DeleteGame(CurrSave)
			GameSaved = False
			LoadSavedGames()
		EndIf
		
		me\Terminated = True
		ShowEntity(me\Head)
		PositionEntity(me\Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity(me\Head)
		RotateEntity(me\Head, 0.0, EntityYaw(Camera), 0.0)
	EndIf
End Function

Function InjurePlayer%(Injuries_#, Infection# = 0.0, BlurTimer_# = 0.0, VestFactor# = 0.0, HelmetFactor# = 0.0)
	me\Injuries = me\Injuries + Injuries_ - ((wi\BallisticVest = 1) * VestFactor) - ((wi\BallisticVest = 2) * VestFactor * 1.4) - (me\Crouch * wi\BallisticHelmet * HelmetFactor)
	me\BlurTimer = me\BlurTimer + BlurTimer_
	I_008\Timer = I_008\Timer + (Infection * (wi\HazmatSuit = 0))
End Function

Function UpdateCough%(Chance_%)
	If (Not me\Terminated)
		If Rand(Chance_) = 1
			If (Not ChannelPlaying(CoughCHN))
				CoughCHN = PlaySound_Strict(CoughSFX((wi\GasMask > 0) Lor (I_1499\Using > 0) Lor (wi\HazmatSuit > 0), Rand(0, 2)), True)
				me\SndVolume = Max(4.0, me\SndVolume)
			EndIf
		EndIf
	EndIf
	If ChannelPlaying(CoughCHN) Then StopBreathSound()
End Function

Function MakeMeUnplayable%()
	If me\Playable
		NullSelectedStuff()
		me\Playable = False
	EndIf
End Function

Function InteractObject%(OBJ%, Dist#, Arrow% = False, ArrowID% = 0, MouseDown_% = False)
	If MenuOpen Lor InvOpen Lor ConsoleOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated Then Return
	
	If EntityDistanceSquared(me\Collider, OBJ) < Dist
		If EntityInView(OBJ, Camera)
			DrawArrowIcon[ArrowID] = Arrow
			DrawHandIcon = True
			If MouseDown_
				If mo\MouseDown1 Then Return(True)
			Else
				If mo\MouseHit1 Then Return(True)
			EndIf
		EndIf
	EndIf
	Return(False)
End Function

Function SetCrouch%(NewCrouch%)
	Local Temp%
	
	If me\Stamina > 0.0
		If NewCrouch <> me\Crouch
			PlaySound_Strict(CrouchSFX)
			me\Stamina = me\Stamina - Rnd(8.0, 16.0)
			me\SndVolume = Max(2.0, me\SndVolume)
			
			If me\Stamina < 10.0
				If (Not ChannelPlaying(BreathCHN))
					Temp = 0
					If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
					BreathCHN = PlaySound_Strict(BreathSFX((Temp), 0), True)
				EndIf
			EndIf
			
			me\Crouch = NewCrouch
		EndIf
	EndIf
End Function

Function UpdateMoving%()
	CatchErrors("UpdateMoving()")
	
	Local de.Decals
	Local Sprint# = 1.0, Speed# = 0.018
	Local Pvt%, i%, Angle#
	
	If chs\SuperMan
		Speed = Speed * 3.0
		
		chs\SuperManTimer = chs\SuperManTimer + fps\Factor[0]
		
		me\CameraShake = Sin(chs\SuperManTimer / 5.0) * (chs\SuperManTimer / 1500.0)
		
		If chs\SuperManTimer > 70.0 * 50.0
			msg\DeathMsg = GetLocalString("console", "superman")
			Kill()
			;If EntityHidden(t\OverlayID[0]) Then ShowEntity(t\OverlayID[0])
		Else
			me\BlurTimer = 500.0
			;If (Not EntityHidden(t\OverlayID[0])) Then HideEntity(t\OverlayID[0])
		EndIf
	EndIf
	
	If me\DeathTimer > 0.0
		me\DeathTimer = me\DeathTimer - fps\Factor[0]
		If me\DeathTimer < 1.0 Then me\DeathTimer = -1.0
	ElseIf me\DeathTimer < 0.0 
		Kill()
	EndIf
	
	If me\Stamina < me\StaminaMax
		If me\CurrSpeed > 0.0
			me\Stamina = Min(me\Stamina + (0.15 * fps\Factor[0] / 1.25), 100.0)
		Else
			me\Stamina = Min(me\Stamina + (0.15 * fps\Factor[0] * 1.25), 100.0)
		EndIf
	EndIf
	
	If me\StaminaEffectTimer > 0.0
		me\StaminaEffectTimer = me\StaminaEffectTimer - (fps\Factor[0] / 70.0)
	Else
		me\StaminaEffect = 1.0
	EndIf
	
	Local Temp#, Temp3%
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	
	If (Not me\Terminated) And (Not chs\NoClip) And (RID <> r_dimension_106) And (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null)
		If me\Stamina < 5.0
			If (Not ChannelPlaying(BreathCHN))
				Temp3 = 0
				If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp3 = 1
				BreathCHN = PlaySound_Strict(BreathSFX((Temp3), 0), True)
				ChannelVolume(BreathCHN, opt\VoiceVolume * opt\MasterVolume)
			EndIf
		ElseIf me\Stamina < 40.0
			If (Not ChannelPlaying(BreathCHN))
				Temp3 = 0
				If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp3 = 1
				BreathCHN = PlaySound_Strict(BreathSFX((Temp3), Rand(3)), True)
				ChannelVolume(BreathCHN, Min((70.0 - me\Stamina) / 70.0, 1.0) * opt\VoiceVolume * opt\MasterVolume)
			EndIf
		EndIf
	EndIf
	
	me\StaminaMax = 100.0
	
	If I_714\Using = 2
		me\Stamina = CurveValue(Min(10.0, me\Stamina), me\Stamina, 10.0)
		me\StaminaMax = Min(me\StaminaMax, 10.0)
		me\Sanity = Max(-850.0, me\Sanity)
	ElseIf I_714\Using = 1
		me\Stamina = CurveValue(Min(25.0, me\Stamina), me\Stamina, 15.0)
		me\StaminaMax = Min(me\StaminaMax, 25.0)
	Else
		If wi\BallisticVest = 2 Lor wi\HazmatSuit = 1
			me\Stamina = CurveValue(Min(60.0, me\Stamina), me\Stamina, 20.0)
			me\StaminaMax = Min(me\StaminaMax, 60.0)
		EndIf
		If wi\GasMask = 3 Lor wi\HazmatSuit = 3 Lor I_1499\Using = 2 Then me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.002 * fps\Factor[0])
		If wi\GasMask = 4 Lor wi\HazmatSuit = 4 Then me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.01 * fps\Factor[0])
	EndIf
	
	If me\Zombie
		If me\Crouch Then SetCrouch(False)
	EndIf
	
	If Abs(me\CrouchState - me\Crouch) < 0.001
		me\CrouchState = me\Crouch
	Else
		me\CrouchState = CurveValue(me\Crouch, me\CrouchState, 10.0)
	EndIf
	
	If (Not (d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor I_294\Using))
		If (Not chs\NoClip)
			If me\Playable And me\FallTimer >= 0.0 And (Not me\Terminated)
				If (KeyDown(key\MOVEMENT_DOWN) Xor KeyDown(key\MOVEMENT_UP)) Lor (KeyDown(key\MOVEMENT_RIGHT) Xor KeyDown(key\MOVEMENT_LEFT)) Lor me\ForceMove > 0.0 
					If (Not me\Crouch) And (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) And me\Stamina > 0.0
						me\Stamina = me\Stamina - (fps\Factor[0] * 0.4 * me\StaminaEffect)
						If me\Stamina <= 0.0 Then me\Stamina = -20.0
						Sprint = 2.5
					EndIf
					
					If RID = r_dimension_106
						Local PlayerPosY# = EntityY(me\Collider)
						
						If PlayerPosY < 2000.0 * RoomScale Lor PlayerPosY > 2608.0 * RoomScale
							me\Stamina = 0.0
							Speed = 0.015
							Sprint = 1.0
						EndIf
					EndIf
					
					If InvOpen Lor OtherOpen <> Null Then Speed = 0.009
					
					If me\ForceMove > 0.0 Then Speed = Speed * me\ForceMove
					
					If SelectedItem <> Null
						If (SelectedItem\ItemTemplate\TempName = "firstaid" Lor SelectedItem\ItemTemplate\TempName = "finefirstaid" Lor SelectedItem\ItemTemplate\TempName = "firstaid2") And wi\HazmatSuit = 0 Then Sprint = 0.0
					EndIf
					
					Temp = (me\Shake Mod 360.0)
					
					If me\Playable Then me\Shake = ((me\Shake + fps\Factor[0] * Min(Sprint, 1.5) * 7.0) Mod 720.0)
					If Temp < 180.0 And (me\Shake Mod 360.0) >= 180.0
						Temp = GetStepSound(me\Collider)
						If DecalStep = 1
							Temp = 2
						ElseIf forest_event <> Null
							If forest_event\room = PlayerRoom And forest_event\EventState = 1.0 Then Temp = 4 ; ~ Improve somehow in future
						EndIf
						
						Local TempCHN% = 0, TempCHN2% = 0
						Local HasSprint% = True, StepRand% = Rand(0, 7)
						
						Select Temp
							Case 2, 3, 4
								;[Block]
								HasSprint = False
								StepRand = Rand(0, 2)
								;[End Block]
						End Select
						
						TempCHN = PlaySound_Strict(StepSFX(Temp, (Sprint = 2.5 And HasSprint), StepRand))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume * opt\MasterVolume)
						If DecalStep = 2
							TempCHN2 = PlaySound_Strict(Step2SFX[Rand(10, 11)])
							ChannelVolume(TempCHN2, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume * opt\MasterVolume)
						EndIf
						
						If Sprint = 2.5
							me\SndVolume = Max(4.0, me\SndVolume)
						Else
							me\SndVolume = Max(2.5 - (me\Crouch * 0.6), me\SndVolume)
						EndIf
					EndIf
				EndIf
				If KeyHit(key\CROUCH) And (Not me\Zombie) And me\Bloodloss < 60.0 And I_427\Timer < 70.0 * 390.0 And (SelectedItem = Null Lor (SelectedItem\ItemTemplate\TempName <> "firstaid" And SelectedItem\ItemTemplate\TempName <> "finefirstaid" And SelectedItem\ItemTemplate\TempName <> "firstaid2")) Then SetCrouch((Not me\Crouch))
			EndIf
		Else
			If (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null)
				Sprint = 2.5
			ElseIf KeyDown(key\CROUCH)
				Sprint = 0.5
			EndIf
		EndIf
		
		Local Temp2# = (Speed * Sprint) / (1.0 + me\CrouchState)
		
		If chs\NoClip
			me\Shake = 0.0
			me\CurrSpeed = 0.0
			me\Crouch = False
			
			RotateEntity(me\Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0.0)
			
			Temp2 = Temp2 * chs\NoClipSpeed
			
			If KeyDown(key\MOVEMENT_DOWN) Then MoveEntity(me\Collider, 0.0, 0.0, (-Temp2) * fps\Factor[0])
			If KeyDown(key\MOVEMENT_UP) Then MoveEntity(me\Collider, 0.0, 0.0, Temp2 * fps\Factor[0])
			
			If KeyDown(key\MOVEMENT_LEFT) Then MoveEntity(me\Collider, (-Temp2) * fps\Factor[0], 0.0, 0.0)
			If KeyDown(key\MOVEMENT_RIGHT) Then MoveEntity(me\Collider, Temp2 * fps\Factor[0], 0.0, 0.0)
			
			ResetEntity(me\Collider)
		Else
			Temp2 = Temp2 / Max((me\Injuries + 3.0) / 3.0, 1.0)
			If me\Injuries > 0.5 Then Temp2 = Temp2 * Min((Sin(me\Shake / 2.0) + 1.2), 1.0)
			Temp = False
			If (Not me\Zombie) And me\FallTimer >= 0.0
				If KeyDown(key\MOVEMENT_DOWN) And me\Playable
					If (Not KeyDown(key\MOVEMENT_UP))
						Temp = True
						Angle = 180.0
						If KeyDown(key\MOVEMENT_LEFT)
							If (Not KeyDown(key\MOVEMENT_RIGHT)) Then Angle = 135.0
						ElseIf KeyDown(key\MOVEMENT_RIGHT)
							Angle = -135.0
						EndIf
					Else
						If KeyDown(key\MOVEMENT_LEFT)
							If (Not KeyDown(key\MOVEMENT_RIGHT))
								Temp = True
								Angle = 90.0
							EndIf
						ElseIf KeyDown(key\MOVEMENT_RIGHT)
							Temp = True
							Angle = -90.0
						EndIf
					EndIf
				ElseIf KeyDown(key\MOVEMENT_UP) And me\Playable
					Temp = True
					Angle = 0.0
					If KeyDown(key\MOVEMENT_LEFT)
						If (Not KeyDown(key\MOVEMENT_RIGHT)) Then Angle = 45.0
					ElseIf KeyDown(key\MOVEMENT_RIGHT)
						Angle = -45.0
					EndIf
				ElseIf me\ForceMove > 0.0
					Temp = True
					Angle = me\ForceAngle
				ElseIf me\Playable
					If KeyDown(key\MOVEMENT_LEFT)
						If (Not KeyDown(key\MOVEMENT_RIGHT))
							Temp = True
							Angle = 90.0
						EndIf
					ElseIf KeyDown(key\MOVEMENT_RIGHT)
						Temp = True
						Angle = -90.0
					EndIf
				EndIf
			Else
				Temp = True
				Angle = me\ForceAngle
			EndIf
			
			Angle = WrapAngle(EntityYaw(me\Collider, True) + Angle + 90.0)
			
			If Temp
				me\CurrSpeed = CurveValue(Temp2, me\CurrSpeed, 20.0)
			Else
				me\CurrSpeed = Max(CurveValue(0.0, me\CurrSpeed - 0.1, 1.0), 0.0)
			EndIf
			
			If me\Playable Then TranslateEntity(me\Collider, Cos(Angle) * me\CurrSpeed * fps\Factor[0], 0.0, Sin(Angle) * me\CurrSpeed * fps\Factor[0], True)
			
			Local CollidedFloor% = False
			
			For i = 1 To CountCollisions(me\Collider)
				If CollisionY(me\Collider, i) < EntityY(me\Collider) - 0.25
					CollidedFloor = True
					Exit
				EndIf
			Next
			
			If CollidedFloor
				If me\DropSpeed < -0.07
					Temp = GetStepSound(me\Collider)
					If DecalStep = 1
						Temp = 2
					ElseIf forest_event <> Null
						If forest_event\room = PlayerRoom And forest_event\EventState = 1.0 Then Temp = 4 ; ~ Improve somehow in future
					EndIf
					
					TempCHN = 0 : TempCHN2 = 0
					HasSprint = True : StepRand = Rand(0, 7)
					
					Select Temp
						Case 2, 3, 4
							;[Block]
							HasSprint = False
							StepRand = Rand(0, 2)
							;[End Block]
					End Select
					
					TempCHN = PlaySound_Strict(StepSFX(Temp, (Sprint = 2.5 And HasSprint), StepRand))
					ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume * opt\MasterVolume)
					If DecalStep = 2
						TempCHN2 = PlaySound_Strict(Step2SFX[Rand(10, 11)])
						ChannelVolume(TempCHN2, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume * opt\MasterVolume)
					EndIf
				EndIf
				me\DropSpeed = 0.0
			Else
				If PlayerFallingPickDistance <> 0.0
					Local Pick# = LinePick(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0.0, -PlayerFallingPickDistance, 0.0)
					
					If Pick
						me\DropSpeed = Min(Max(me\DropSpeed - (0.006 * fps\Factor[0]), -2.0), 0.0)
					Else
						me\DropSpeed = 0.0
					EndIf
				Else
					me\DropSpeed = Min(Max(me\DropSpeed - (0.006 * fps\Factor[0]), -2.0), 0.0)
				EndIf
			EndIf
			PlayerFallingPickDistance = 10.0
			
			If me\Playable And ShouldEntitiesFall Then TranslateEntity(me\Collider, 0.0, me\DropSpeed * fps\Factor[0], 0.0)
		EndIf
		me\ForceMove = 0.0
	EndIf
	
	If me\Injuries > 1.0
		Temp2 = me\Bloodloss
		me\BlurTimer = Max(Max(Sin(MilliSec / 100.0) * me\Bloodloss * 30.0, me\Bloodloss * 2.0 * (2.0 - me\CrouchState)), me\BlurTimer)
		If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then me\Bloodloss = Min(me\Bloodloss + (Min(me\Injuries, 3.5) / 300.0) * fps\Factor[0], 100.0)
		If Temp2 <= 60.0 And me\Bloodloss > 60.0 Then CreateMsg(GetLocalString("msg", "bloodloss"))
	EndIf
	
	Update008()
	Update409()
	
	If me\Bloodloss > 0.0 And me\VomitTimer >= 0.0
		If Rnd(200.0) < Min(me\Injuries, 4.0)
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(me\Collider) + Rnd(-0.05, 0.05), EntityY(me\Collider) - 0.05, EntityZ(me\Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			
			de.Decals = CreateDecal(Rand(DECAL_BLOOD_DROP_1, DECAL_BLOOD_DROP_2), PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, Rnd(0.03, 0.08) * Min(me\Injuries, 2.5))
			de\SizeChange = Rnd(0.001, 0.0015) : de\MaxSize = de\Size + Rnd(0.008, 0.009)
			EntityParent(de\OBJ, PlayerRoom\OBJ)
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 3)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * opt\SFXVolume * opt\MasterVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			
			FreeEntity(Pvt) : Pvt = 0
		EndIf
		
		me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * me\Bloodloss * 0.2)
		
		If me\Bloodloss > 60.0 And (Not chs\NoClip)
			If (Not me\Crouch) Then SetCrouch(True)
		EndIf
		If me\Bloodloss >= 100.0
			me\HeartBeatVolume = 0.0
			Kill(True)
		ElseIf me\Bloodloss > 80.0
			me\HeartBeatRate = Max(150.0 - (me\Bloodloss - 80.0) * 5.0, me\HeartBeatRate)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, 0.75 + (me\Bloodloss - 80.0) * 0.0125)
		ElseIf me\Bloodloss > 35.0
			me\HeartBeatRate = Max(70.0 + me\Bloodloss, me\HeartBeatRate)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, (me\Bloodloss - 35.0) / 60.0)
		EndIf
	EndIf
	
	If me\HealTimer > 0.0
		Local FPSFactorEx# = fps\Factor[0] / 70.0
		
		me\HealTimer = Max(me\HealTimer - FPSFactorEx, 0.0)
		me\Bloodloss = Min(me\Bloodloss + FPSFactorEx / 3.0, 100.0)
		me\Injuries = Max(me\Injuries - FPSFactorEx / 30.0, 0.0)
	EndIf
		
	If me\Playable
		If KeyHit(key\BLINK) Then me\BlinkTimer = 0.0
		If KeyDown(key\BLINK) And me\BlinkTimer < -10.0 Then me\BlinkTimer = -10.0
	EndIf
	
	If me\HeartBeatVolume > 0.0
		If me\HeartBeatTimer <= 0.0
			TempCHN = PlaySound_Strict(HeartBeatSFX)
			ChannelVolume(TempCHN, me\HeartBeatVolume * opt\SFXVolume * opt\MasterVolume)
			
			me\HeartBeatTimer = 70.0 * (60.0 / Max(me\HeartBeatRate, 1.0))
		Else
			me\HeartBeatTimer = me\HeartBeatTimer - fps\Factor[0]
		EndIf
		me\HeartBeatVolume = Max(me\HeartBeatVolume - fps\Factor[0] * 0.05, 0.0)
	EndIf
	
	CatchErrors("Uncaught: UpdateMoving()")
End Function

Type WearableItems
	Field GasMask%, GasMaskFogTimer#
	Field HazmatSuit%
	Field BallisticVest%
	Field BallisticHelmet%
	Field NightVision%, NVGTimer#, IsNVGBlinking%
	Field SCRAMBLE%
End Type

Global wi.WearableItems

Global CameraPitch#

Function UpdateMouseLook%()
	CatchErrors("UpdateMouseLook()")
	
	Local p.Particles
	Local i%
	Local FPSFactorEx# = fps\Factor[0] / 10.0
	
	me\CameraShake = Max(me\CameraShake - FPSFactorEx, 0.0)
	me\BigCameraShake = Max(me\BigCameraShake - FPSFactorEx, 0.0)
	
	CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / (Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * (RealGraphicWidthFloat / RealGraphicHeightFloat))) / 2.0)))
	me\CurrCameraZoom = Max(me\CurrCameraZoom - fps\Factor[0], 0.0)
	
	If (Not me\Terminated) And me\FallTimer >= 0.0
		me\HeadDropSpeed = 0.0
		
		If IsNaN(EntityX(me\Collider))
			PositionEntity(me\Collider, EntityX(Camera, True), EntityY(Camera, True) - 0.5, EntityZ(Camera, True), True)
			CreateConsoleMsg(Format(GetLocalString("console", "xyz.reset"), EntityX(me\Collider)))
		EndIf
		
		Local Up# = (Sin(me\Shake) / (20.0 + me\CrouchState * 20.0)) * 0.6
		Local Roll# = Max(Min(Sin(me\Shake / 2.0) * 2.5 * Min(me\Injuries + 0.25, 3.0), 8.0), -8.0)
		
		PositionEntity(Camera, EntityX(me\Collider), EntityY(me\Collider) + Up + 0.6 + me\CrouchState * (-0.3), EntityZ(me\Collider))
		RotateEntity(Camera, 0.0, EntityYaw(me\Collider), Roll * 0.5)
		
		; ~ Update the smoothing que to smooth the movement of the mouse
		Local Temp# = (opt\MouseSensitivity + 0.5)
		Local Temp2# = (5.0 / (opt\MouseSensitivity + 1.0)) * opt\MouseSmoothing
		
		If opt\InvertMouseX
			mo\Mouse_X_Speed_1 = CurveValue(-MouseXSpeed() * Temp, mo\Mouse_X_Speed_1, Temp2)
		Else
			mo\Mouse_X_Speed_1 = CurveValue(MouseXSpeed() * Temp, mo\Mouse_X_Speed_1, Temp2)
		EndIf
		If IsNaN(mo\Mouse_X_Speed_1) Then mo\Mouse_X_Speed_1 = 0.0
		If opt\InvertMouseY
			mo\Mouse_Y_Speed_1 = CurveValue(-MouseYSpeed() * Temp, mo\Mouse_Y_Speed_1, Temp2)
		Else
			mo\Mouse_Y_Speed_1 = CurveValue(MouseYSpeed() * Temp, mo\Mouse_Y_Speed_1, Temp2)
		EndIf
		If IsNaN(mo\Mouse_Y_Speed_1) Then mo\Mouse_Y_Speed_1 = 0.0
		
		If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Then StopMouseMovement()
		
		Local The_Yaw# = ((mo\Mouse_X_Speed_1)) * mo\Mouselook_X_Inc / (1.0 + wi\BallisticVest)
		Local The_Pitch# = ((mo\Mouse_Y_Speed_1)) * mo\Mouselook_Y_Inc / (1.0 + wi\BallisticVest)
		
		TurnEntity(me\Collider, 0.0, -The_Yaw, 0.0) ; ~ Turn the user on the Y (Yaw) axis
		CameraPitch = CameraPitch + The_Pitch
		; ~ Limit the user's camera to within 180.0 degrees of pitch rotation. Returns useless values so we need to use a variable to keep track of the camera pitch
		If CameraPitch > 70.0 Then CameraPitch = 70.0
		If CameraPitch < -70.0 Then CameraPitch = -70.0
		
		Local ShakeTimer# = me\CameraShake + me\BigCameraShake
		
		RotateEntity(Camera, WrapAngle(CameraPitch + Rnd(-ShakeTimer, ShakeTimer)), WrapAngle(EntityYaw(me\Collider) + Rnd(-ShakeTimer, ShakeTimer)), Roll) ; ~ Pitch the user's camera up and down
		
		If PlayerRoom\RoomTemplate\RoomID = r_dimension_106
			Local PlayerPosY# = EntityY(me\Collider)
			
			If PlayerPosY < 2000.0 * RoomScale Lor PlayerPosY > 2608.0 * RoomScale Then RotateEntity(Camera, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), Roll + WrapAngle(Sin(MilliSec / 150.0) * 30.0)) ; ~ Pitch the user's camera up and down
		EndIf
	Else
		If (Not EntityHidden(me\Collider)) Then HideEntity(me\Collider)
		PositionEntity(Camera, EntityX(me\Head), EntityY(me\Head), EntityZ(me\Head))
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(me\Head)
			If CollisionY(me\Head, i) < EntityY(me\Head) - 0.01
				CollidedFloor = True
				Exit
			EndIf
		Next
		
		If CollidedFloor
			me\HeadDropSpeed = 0.0
		Else
			If (Not me\KillAnim)
				MoveEntity(me\Head, 0.0, 0.0, me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(-90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity(me\Head, 0.0, 0.0, -me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			me\HeadDropSpeed = me\HeadDropSpeed - (0.002 * fps\Factor[0])
		EndIf
	EndIf
	
	UpdateDust()
	
	; ~ Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop
	If (Not (MenuOpen Lor InvOpen Lor ConsoleOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null))
		If (MousePosX > mo\Mouse_Right_Limit) Lor (MousePosX < mo\Mouse_Left_Limit) Lor (MousePosY > mo\Mouse_Bottom_Limit) Lor (MousePosY < mo\Mouse_Top_Limit) Then MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	EndIf
	
	If wi\GasMask > 0 Lor wi\HazmatSuit > 0 Lor I_1499\Using > 0
		If wi\HazmatSuit > 0
			If EntityHidden(t\OverlayID[2]) Then ShowEntity(t\OverlayID[2])
		Else
			If EntityHidden(t\OverlayID[1]) Then ShowEntity(t\OverlayID[1])
		EndIf
		
		If (Not me\Terminated)
			If (Not ChannelPlaying(BreathCHN))
				If (Not ChannelPlaying(BreathGasRelaxedCHN))
					BreathGasRelaxedCHN = PlaySound_Strict(BreathGasRelaxedSFX, True)
					ChannelVolume(BreathGasRelaxedCHN, opt\VoiceVolume * opt\MasterVolume)
				EndIf
			Else
				If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
			EndIf
		EndIf
		
		If wi\GasMask <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 2 And wi\HazmatSuit <> 4
			; ~ TODO: Make more realistic
			If ChannelPlaying(BreathCHN)
				wi\GasMaskFogTimer = Min(wi\GasMaskFogTimer + (fps\Factor[0] * Rnd(0.5, 1.6)), 100.0)
			Else
				wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fps\Factor[0] * 0.3))
			EndIf
			If EntityHidden(t\OverlayID[10]) Then ShowEntity(t\OverlayID[10])
			EntityAlpha(t\OverlayID[10], Min(PowTwo(wi\GasMaskFogTimer * 0.2) / 1000.0, 0.45))
		EndIf
	Else
		If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
		wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fps\Factor[0] * 0.3))
		If (Not EntityHidden(t\OverlayID[1])) Then HideEntity(t\OverlayID[1])
		If (Not EntityHidden(t\OverlayID[2])) Then HideEntity(t\OverlayID[2])
		If (Not EntityHidden(t\OverlayID[10])) Then HideEntity(t\OverlayID[10])
	EndIf
	
	If wi\BallisticHelmet
		If EntityHidden(t\OverlayID[8]) Then ShowEntity(t\OverlayID[8])
	Else
		If (Not EntityHidden(t\OverlayID[8])) Then HideEntity(t\OverlayID[8])
	EndIf
	
	If wi\NightVision > 0 Lor wi\SCRAMBLE > 0
		If EntityHidden(t\OverlayID[4]) Then ShowEntity(t\OverlayID[4])
		If wi\NightVision = 2
			EntityColor(t\OverlayID[4], 0.0, 100.0, 200.0)
		ElseIf wi\NightVision = 3
			EntityColor(t\OverlayID[4], 200.0, 0.0, 0.0)
		ElseIf wi\NightVision = 1
			EntityColor(t\OverlayID[4], 0.0, 200.0, 0.0)
		Else
			EntityColor(t\OverlayID[4], 200.0, 200.0, 200.0)
		EndIf
		;EntityTexture(t\OverlayID[0], t\OverlayTextureID[12])
	Else
		If (Not EntityHidden(t\OverlayID[4])) Then HideEntity(t\OverlayID[4])
		;EntityTexture(t\OverlayID[0], t\OverlayTextureID[0])
	EndIf
	
	Update1025()
	
	CatchErrors("Uncaught: UpdateMouseLook()")
End Function

; ~ Fog Constants
;[Block]
Const FogColorLCZ$ = "005005005"
Const FogColorHCZ$ = "007002002"
Const FogColorEZ$ = "007007012"
Const FogColorStorageTunnels$ = "002007000"
Const FogColorOutside$ = "255255255"
Const FogColorDimension_1499$ = "096097104"
Const FogColorPD$ = "000000000"
Const FogColorPDTrench$ = "038055047"
Const FogColorForest$ = "098133162"
Const FogColorForestChase$ = "032044054"
;[End Block]

Global CurrFogColor$
Global CurrFogColorR#, CurrFogColorG#, CurrFogColorB#

; ~ Ambient Color Constants
;[Block]
Const AmbientColorLCZ$ = "030030030"
Const AmbientColorHCZ$ = "030023023"
Const AmbientColorEZ$ = "023023030"
;[End Block]

Global CurrAmbientColor$
Global CurrAmbientColorR#, CurrAmbientColorG#, CurrAmbientColorB#

Const ZoneColorChangeSpeed# = 50.0

Function SetZoneColor%(FogColor$, AmbientColor$ = AmbientColorLCZ)
	CurrFogColor = FogColor
	CurrAmbientColor = AmbientColor
End Function

Function UpdateZoneColor%()
	Local e.Events
	Local i%
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	Local PlayerPosY# = EntityY(me\Collider, True)
	
	CurrFogColor$ = ""
	CurrAmbientColor$ = ""
	
	CameraFogMode(Camera, 1)
	If opt\DebugMode = 1
		CameraRange(Camera, 0.01, 100.0)
	Else
		CameraRange(Camera, 0.01, Min(opt\CameraFogFar * LightVolume * 1.5, HideDistance * 1.2))
	EndIf
	CameraFogRange(Camera, 0.1 * LightVolume, opt\CameraFogFar * LightVolume)
	; ~ Handle room-specific settings
	If RID = r_room3_storage And PlayerPosY < (-4100.0) * RoomScale
		SetZoneColor(FogColorStorageTunnels)
	ElseIf IsPlayerOutsideFacility()
		SetZoneColor(FogColorOutside)
		CameraFogRange(Camera, 5.0, 30.0)
		CameraRange(Camera, 0.01, 60.0)
	ElseIf RID = r_cont1_173_intro
		CameraFogRange(Camera, 5.0, 30.0)
		CameraRange(Camera, 0.01, 45.0)
	ElseIf RID = r_dimension_1499
		SetZoneColor(FogColorDimension_1499)
		CameraFogRange(Camera, 40.0, 80.0)
		CameraRange(Camera, 0.01, 90.0)
	ElseIf RID = r_dimension_106
		For e.Events = Each Events
			If e\EventID = e_dimension_106
				If e\EventState2 = PD_TrenchesRoom Lor e\EventState2 = PD_TowerRoom
					SetZoneColor(FogColorPDTrench)
				ElseIf e\EventState2 = PD_FakeTunnelRoom
					SetZoneColor(FogColorHCZ, AmbientColorHCZ)
				Else
					SetZoneColor(FogColorPD)
				EndIf
				Exit
			EndIf
		Next
	ElseIf (RID = r_room2_mt And (PlayerPosY >= 8.0 And PlayerPosY <= 12.0)) Lor (RID = r_cont2_409 And PlayerPosY < (-3728.0) * RoomScale) Lor (RID = r_cont1_895 And PlayerPosY < (-1200.0) * RoomScale)
		SetZoneColor(FogColorHCZ, AmbientColorHCZ)
	ElseIf forest_event <> Null
		If PlayerRoom = forest_event\room
			If forest_event\EventState = 1.0
				SetZoneColor(FogColorForest)
				If forest_event\room\NPC[0] <> Null
					If forest_event\room\NPC[0]\State >= 2.0 Then SetZoneColor(FogColorForestChase)
				EndIf
				CameraRange(Camera, 0.01, 8.5)
				CameraFogRange(Camera, 0.01, 8.0)
			EndIf
		EndIf
	EndIf
	
	; ~ If unset, use standard settings based on zone
	If CurrFogColor = ""
		Select me\Zone
			Case 0
				;[Block]
				SetZoneColor(FogColorLCZ, AmbientColorLCZ)
				;[End Block]
			Case 1
				;[Block]
				SetZoneColor(FogColorHCZ, AmbientColorHCZ)
				;[End Block]
			Case 2
				;[Block]
				SetZoneColor(FogColorEZ, AmbientColorEZ)
				;[End Block]
		End Select
	EndIf
	
	CurrFogColorR = CurveValue(Left(CurrFogColor, 3), CurrFogColorR, ZoneColorChangeSpeed)
	CurrFogColorG = CurveValue(Mid(CurrFogColor, 4, 3), CurrFogColorG, ZoneColorChangeSpeed)
	CurrFogColorB = CurveValue(Right(CurrFogColor, 3), CurrFogColorB, ZoneColorChangeSpeed)
	
	CameraFogColor(Camera, CurrFogColorR, CurrFogColorG, CurrFogColorB)
	CameraClsColor(Camera, CurrFogColorR, CurrFogColorG, CurrFogColorB)
	
	CurrAmbientColorR = CurveValue(Left(CurrAmbientColor, 3), CurrAmbientColorR, ZoneColorChangeSpeed)
	CurrAmbientColorG = CurveValue(Mid(CurrAmbientColor, 4, 3), CurrAmbientColorG, ZoneColorChangeSpeed)
	CurrAmbientColorB = CurveValue(Right(CurrAmbientColor, 3), CurrAmbientColorB, ZoneColorChangeSpeed)
	
	Local CurrR#, CurrG#, CurrB#
	
	If wi\NightVision > 0
		CurrR = CurrAmbientColorR * 6.0 : CurrG = CurrAmbientColorG * 6.0 : CurrB = CurrAmbientColorB * 6.0
		AmbientLightRooms(CurrR / 3.0, CurrG / 3.0, CurrB / 3.0)
	ElseIf wi\SCRAMBLE > 0
		CurrR = CurrAmbientColorR * 2.0 : CurrG = CurrAmbientColorG * 2.0 : CurrB = CurrAmbientColorB * 2.0
		AmbientLightRooms(CurrR / 3.0, CurrG / 3.0, CurrB / 3.0)
	Else
		AmbientLightRooms(CurrAmbientColorR / 5.0, CurrAmbientColorG / 5.0, CurrAmbientColorB / 5.0)
		CurrR = CurrAmbientColorR : CurrG = CurrAmbientColorG : CurrB = CurrAmbientColorB
		If forest_event <> Null
			If PlayerRoom = forest_event\room
				If forest_event\EventState = 1.0 Then CurrR = 200.0 : CurrG = 200.0 : CurrB = 200.0
			EndIf
		EndIf
	EndIf
	AmbientLight(CurrR, CurrG, CurrB)
End Function

Function NullSelectedStuff%()
	InvOpen = False
	I_294\Using = False
	d_I\SelectedDoor = Null
	SelectedScreen = Null
	sc_I\SelectedMonitor = Null
	SelectedItem = Null
	OtherOpen = Null
	d_I\ClosestButton = 0
	GrabbedEntity = 0
End Function

Global IsUsingRadio%

Global GrabbedEntity%

Global RadioState#[9]
Global RadioState2%[9]
Global RadioState3%[10]

Global DrawHandIcon%
Global DrawArrowIcon%[4]

Global InvOpen%

Function UpdateGUI%()
	CatchErrors("UpdateGUI()")
	
	Local e.Events, it.Items, r.Rooms
	Local Temp%, x%, y%, z%, i%
	Local x2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	
	If RID = r_dimension_106
		For e.Events = Each Events
			If e\room = PlayerRoom
				If (wi\NightVision > 0 Lor wi\SCRAMBLE > 0) And e\EventState2 <> PD_FakeTunnelRoom
					If e\Img2 <> 0
						StopChannel(e\SoundCHN)
						FreeImage(e\Img2) : e\Img2 = 0
					EndIf
					
					If e\Img = 0
						StopChannel(e\SoundCHN) : e\SoundCHN = 0
						Select Rand(5)
							Case 1
								;[Block]
								PlaySound_Strict(HorrorSFX[1])
								;[End Block]
							Case 2
								;[Block]
								PlaySound_Strict(HorrorSFX[2])
								;[End Block]
							Case 3
								;[Block]
								PlaySound_Strict(HorrorSFX[9])
								;[End Block]
							Case 4
								;[Block]
								PlaySound_Strict(HorrorSFX[10])
								;[End Block]
							Case 5
								;[Block]
								PlaySound_Strict(HorrorSFX[14])
								;[End Block]
						End Select
						e\Img = LoadImage_Strict("GFX\Overlays\scp_106_face_overlay.png")
						e\Img = ScaleImage2(e\Img, MenuScale, MenuScale)
					Else
						wi\IsNVGBlinking = True
						If Rand(30) = 1
							If (Not ChannelPlaying(e\SoundCHN)) Then e\SoundCHN = PlaySound_Strict(DripSFX[Rand(0, 3)])
						EndIf
					EndIf
				Else
					If e\Img <> 0
						StopChannel(e\SoundCHN)
						FreeImage(e\Img) : e\Img = 0
					EndIf
					
					If e\EventState2 = PD_ThroneRoom
						If me\BlinkTimer > -16.0 And me\BlinkTimer < -6.0
							If e\Img2 = 0
								StopChannel(e\SoundCHN) : e\SoundCHN = 0
								PlaySound_Strict(e\Sound2, True)
								e\Img2 = LoadImage_Strict("GFX\Overlays\kneel_mortal_overlay.png")
								e\Img2 = ScaleImage2(e\Img2, MenuScale, MenuScale)
							Else
								If (Not ChannelPlaying(e\SoundCHN))
									e\SoundCHN = PlaySound_Strict(e\Sound)
									ChannelVolume(e\SoundCHN, opt\VoiceVolume * opt\MasterVolume)
								EndIf
							EndIf
						Else
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
						EndIf
					Else
						If e\Img2 <> 0
							FreeImage(e\Img2) : e\Img2 = 0
							StopChannel(e\SoundCHN) : e\SoundCHN = 0
						EndIf
					EndIf
				EndIf
				Exit
			EndIf
		Next
	EndIf
	
	If I_294\Using Then Update294()
	If (Not (MenuOpen Lor InvOpen Lor ConsoleOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated))
		If d_I\ClosestButton <> 0
			If mo\MouseUp1
				mo\MouseUp1 = False
				If d_I\ClosestDoor <> Null
					If d_I\ClosestDoor\Code <> ""
						d_I\SelectedDoor = d_I\ClosestDoor
					ElseIf me\Playable
						UseDoor()
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	If (Not (MenuOpen Lor me\Terminated))
		If SelectedScreen <> Null
			If mo\MouseUp1 Lor mo\MouseHit2 Then
				FreeImage(SelectedScreen\Img) : SelectedScreen\Img = 0
				mo\MouseUp1 = False
				SelectedScreen = Null
			EndIf
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If d_I\SelectedDoor <> Null
		If SelectedItem <> Null
			If SelectedItem\ItemTemplate\TempName = "scp005"
				UseDoor()
				ShouldDrawHUD = False
			Else
				SelectedItem = Null
			EndIf
		EndIf
		If ShouldDrawHUD
			Local ButtonPosX# = EntityX(d_I\ClosestButton, True)
			Local ButtonPosY# = EntityY(d_I\ClosestButton, True)
			Local ButtonPosZ# = EntityZ(d_I\ClosestButton, True)
			
			CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * opt\RealGraphicWidth / opt\RealGraphicHeight)) / 2.0))
			Pvt = CreatePivot()
			PositionEntity(Pvt, ButtonPosX, ButtonPosY, ButtonPosZ)
			RotateEntity(Pvt, 0.0, EntityYaw(d_I\ClosestButton, True) - 180.0, 0.0)
			MoveEntity(Pvt, 0.0, 0.0, 0.22)
			PositionEntity(Camera, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
			PointEntity(Camera, d_I\ClosestButton)
			FreeEntity(Pvt) : Pvt = 0
			
			CameraProject(Camera, ButtonPosX, ButtonPosY + (MeshHeight(d_I\ButtonModelID[BUTTON_DEFAULT_MODEL]) * 0.015), ButtonPosZ)
			ProjY = ProjectedY()
			CameraProject(Camera, ButtonPosX, ButtonPosY - (MeshHeight(d_I\ButtonModelID[BUTTON_DEFAULT_MODEL]) * 0.015), ButtonPosZ)
			Scale = (ProjectedY() - ProjY) / (462.0 * MenuScale)
			
			x = mo\Viewport_Center_X - ImageWidth(t\ImageID[4]) * (Scale / 2)
			y = mo\Viewport_Center_Y - ImageHeight(t\ImageID[4]) * (Scale / 2)
			
			If msg\KeyPadMsg <> ""
				msg\KeyPadTimer = msg\KeyPadTimer - fps\Factor[0]
				If msg\KeyPadTimer <= 0.0
					msg\KeyPadMsg = ""
					d_I\SelectedDoor = Null
					StopMouseMovement()
				EndIf
			EndIf
			
			If (Not MenuOpen)
				x = x + (44 * MenuScale * Scale)
				y = y + (249 * MenuScale * Scale)
				
				For n = 0 To 3
					For i = 0 To 2
						xTemp = x + ((58.5 * MenuScale * Scale) * n)
						yTemp = y + ((67 * MenuScale * Scale) * i)
						
						Temp = False
						If MouseOn(xTemp, yTemp, 54 * MenuScale * Scale, 65 * MenuScale * Scale) And msg\KeyPadMsg = ""
							If mo\MouseUp1
								PlaySound_Strict(ButtonSFX)
								
								Select (n + 1) + (i * 4)
									Case 1, 2, 3
										;[Block]
										msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4))
										;[End Block]
									Case 4
										;[Block]
										msg\KeyPadInput = msg\KeyPadInput + "0"
										;[End Block]
									Case 5, 6, 7
										;[Block]
										msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4) - 1)
										;[End Block]
									Case 8
										;[Block]
										UseDoor()
										If msg\KeyPadInput = d_I\SelectedDoor\Code
											d_I\SelectedDoor = Null
											StopMouseMovement()
										Else
											msg\KeyPadMsg = GetLocalString("msg", "denied")
											msg\KeyPadTimer = 210.0
											msg\KeyPadInput = ""
										EndIf
										;[End Block]
									Case 9, 10, 11
										;[Block]
										msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4) - 2)
										;[End Block]
									Case 12
										;[Block]
										msg\KeyPadInput = ""
										;[End Block]
								End Select
								If Len(msg\KeyPadInput) > 4 Then msg\KeyPadInput = Left(msg\KeyPadInput, 4)
							EndIf
						Else
							Temp = False
						EndIf
					Next
				Next
				
				If mo\MouseHit2
					d_I\SelectedDoor = Null
					StopMouseMovement()
				EndIf
			EndIf
		Else
			d_I\SelectedDoor = Null
		EndIf
	Else
		msg\KeyPadInput = ""
		msg\KeyPadTimer = 0.0
		msg\KeyPadMsg = ""
	EndIf
	
	If KeyHit(1) And me\EndingTimer >= 0.0 And me\SelectedEnding = -1 And me\KillAnimTimer <= 400.0
		If MenuOpen
			ResumeSounds()
			If igm\OptionsMenu <> 0 Then SaveOptionsINI()
			StopMouseMovement()
			ShouldDeleteGadgets = True
		Else
			PauseSounds()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		igm\AchievementsMenu = 0
		igm\OptionsMenu = 0
		igm\QuitMenu = 0
	EndIf
	
	Local PrevOtherOpen.Items, PrevItem.Items
	Local OtherSize%, OtherAmount%
	Local IsEmpty%
	Local IsMouseOn%
	Local ClosedInv%
	Local INVENTORY_GFX_SIZE% = 70 * MenuScale
	Local INVENTORY_GFX_SPACING% = 35 * MenuScale
	Local MaxItemAmountHalf% = MaxItemAmount / 2
	
	If OtherOpen <> Null
		PrevOtherOpen = OtherOpen
		OtherSize = OtherOpen\InvSlots
		
		For i = 0 To OtherSize - 1
			If OtherOpen\SecondInv[i] <> Null Then OtherAmount = OtherAmount + 1
		Next
		
		InvOpen = False
		d_I\SelectedDoor = Null
		
		Local TempX% = 0
		
		x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * 10 / 2) + (INVENTORY_GFX_SPACING * ((10 / 2) - 1))) / 2
		y = mo\Viewport_Center_Y - (INVENTORY_GFX_SIZE * ((OtherSize / 10 * 2) - 1)) - INVENTORY_GFX_SPACING
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n Then MouseSlot = n
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n]
				If IsMouseOn = n
					If SelectedItem = Null
						If mo\MouseHit1
							SelectedItem = OtherOpen\SecondInv[n]
							
							If mo\DoubleClick And mo\DoubleClickSlot = n
								If SelectedItem\ItemTemplate\TempName = "scp714" Lor SelectedItem\ItemTemplate\TempName = "coarse714" Lor SelectedItem\ItemTemplate\TempName = "fine714" Lor SelectedItem\ItemTemplate\TempName = "ring"
									CreateMsg(GetLocalString("msg", "wallet.714"))
									SelectedItem = Null
									Return
								EndIf
								If OtherOpen\SecondInv[n]\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[OtherOpen\SecondInv[n]\ItemTemplate\SoundID])
								OtherOpen = Null
								ClosedInv = True
								InvOpen = False
								mo\DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And mo\MouseHit1
					For z = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem
							OtherOpen\SecondInv[z] = Null
							Exit
						EndIf
					Next
					OtherOpen\SecondInv[n] = SelectedItem
					SelectedItem = Null
				EndIf
			EndIf
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			TempX = TempX + 1
			If TempX = 5
				TempX = 0
				y = y + (INVENTORY_GFX_SIZE * 2)
				x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * 10 / 2) + (INVENTORY_GFX_SPACING * ((10 / 2) - 1))) / 2
			EndIf
		Next
		
		If mo\MouseHit1 Then mo\DoubleClickSlot = IsMouseOn
		
		If SelectedItem <> Null
			If (Not mo\MouseDown1) Lor mo\MouseHit2
				If MouseSlot = 66
					Local CameraYaw# = EntityYaw(Camera)
					
					If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
					ShowEntity(SelectedItem\Collider)
					PositionEntity(SelectedItem\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\Collider, EntityPitch(Camera), CameraYaw + Rnd(-20.0, 20.0), 0.0)
					MoveEntity(SelectedItem\Collider, 0.0, -0.1, 0.1)
					RotateEntity(SelectedItem\Collider, 0.0, CameraYaw + Rnd(-110.0, 110.0), 0.0)
					ResetEntity(SelectedItem\Collider)
					SelectedItem\Dropped = 1
					SelectedItem\Picked = False
					For z = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem
							OtherOpen\SecondInv[z] = Null
							Exit
						EndIf
					Next
					
					IsEmpty = True
					If OtherOpen\ItemTemplate\TempName = "wallet"
						If (Not IsEmpty)
							For z = 0 To OtherSize - 1
								If OtherOpen\SecondInv[z] <> Null
									Local Name$ = OtherOpen\SecondInv[z]\ItemTemplate\TempName
									
									If Name <> "25ct" And Name <> "coin" And Name <> "key" And Name <> "scp860" And Name <> "scp714" And Name <> "coarse714" And Name <> "fine714" And Name <> "ring" And Name <> "scp500pill" And Name <> "scp500pilldeath" And Name <> "pill"
										IsEmpty = False
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						For z = 0 To OtherSize - 1
							If OtherOpen\SecondInv[z] <> Null
								IsEmpty = False
								Exit
							EndIf
						Next
					EndIf
					
					If IsEmpty
						If OtherOpen\ItemTemplate\TempName = "clipboard"
							OtherOpen\InvImg = OtherOpen\ItemTemplate\InvImg2
							SetAnimTime(OtherOpen\Model, 17.0)
						ElseIf OtherOpen\ItemTemplate\TempName = "wallet"
							SetAnimTime(OtherOpen\Model, 0.0)
						EndIf
					EndIf
					
					SelectedItem = Null
					
					If (Not mo\MouseHit2)
						OtherOpen = Null
						ClosedInv = True
						MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
					EndIf
				Else
					If PrevOtherOpen\SecondInv[MouseSlot] = Null
						For z = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem
								PrevOtherOpen\SecondInv[z] = Null
								Exit
							EndIf
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						PrevItem = PrevOtherOpen\SecondInv[MouseSlot]
						
						Select SelectedItem\ItemTemplate\TempName
							Default
								;[Block]
								For z = 0 To OtherSize - 1
									If PrevOtherOpen\SecondInv[z] = SelectedItem
										PrevOtherOpen\SecondInv[z] = PrevItem
										Exit
									EndIf
								Next
								PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
								SelectedItem = Null
								;[End Block]
						End Select
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If ClosedInv And (Not InvOpen)
			OtherOpen = Null
			StopMouseMovement()
		EndIf
	ElseIf InvOpen
		d_I\SelectedDoor = Null
		
		x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + (INVENTORY_GFX_SPACING * (MaxItemAmountHalf - 1))) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		If MaxItemAmount = 2
			y = y + INVENTORY_GFX_SIZE
			x = x - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + INVENTORY_GFX_SPACING) / 2
		EndIf
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n Then MouseSlot = n
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n)
				If IsMouseOn = n
					If SelectedItem = Null
						If mo\MouseHit1
							SelectedItem = Inventory(n)
							If mo\DoubleClick And mo\DoubleClickSlot = n
								If Inventory(n)\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[Inventory(n)\ItemTemplate\SoundID])
								InvOpen = False
								mo\DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And mo\MouseHit1
					For z = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem
							Inventory(z) = Null
							Exit
						EndIf
					Next
					Inventory(n) = SelectedItem
					SelectedItem = Null
				EndIf
			EndIf
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If MaxItemAmount >= 4 And n = MaxItemAmountHalf - 1
				y = y + (INVENTORY_GFX_SIZE * 2) 
				x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + (INVENTORY_GFX_SPACING * (MaxItemAmountHalf - 1))) / 2
			EndIf
		Next
		
		If mo\MouseHit1 Then mo\DoubleClickSlot = IsMouseOn
		
		If SelectedItem <> Null
			If (Not mo\MouseDown1) Lor mo\MouseHit2
				If MouseSlot = 66
					Local ShouldPreventDropping%
					
					Select SelectedItem\ItemTemplate\TempName
						Case "vest", "finevest", "hazmatsuit", "finehazmatsuit", "veryfinehazmatsuit", "hazmatsuit148"
							;[Block]
							ShouldPreventDropping = True
							;[End Block]
						Case "gasmask"
							;[Block]
							ShouldPreventDropping = (wi\GasMask = 1)
							;[End Block]
						Case "finegasmask"
							;[Block]
							ShouldPreventDropping = (wi\GasMask = 2)
							;[End Block]
						Case "veryfinegasmask"
							;[Block]
							ShouldPreventDropping = (wi\GasMask = 3)
							;[End Block]
						Case "gasmask148"
							;[Block]
							ShouldPreventDropping = (wi\GasMask = 4)
							;[End Block]
						Case "scp1499"
							;[Block]
							ShouldPreventDropping = (I_1499\Using = 1)
							;[End Block]
						Case "fine1499"
							;[Block]
							ShouldPreventDropping = (I_1499\Using = 2)
							;[End Block]
						Case "nvg"
							;[Block]
							ShouldPreventDropping = (wi\NightVision = 1)
							;[End Block]
						Case "veryfinenvg"
							;[Block]
							ShouldPreventDropping = (wi\NightVision = 2)
							;[End Block]
						Case "finenvg"
							;[Block]
							ShouldPreventDropping = (wi\NightVision = 3)
							;[End Block]
						Case "scramble"
							;[Block]
							ShouldPreventDropping = (wi\SCRAMBLE = 1)
							;[End Block]
						Case "finescramble"
							;[Block]
							ShouldPreventDropping = (wi\SCRAMBLE = 2)
							;[End Block]
						Case "helmet"
							;[Block]
							ShouldPreventDropping = wi\BallisticHelmet
							;[End Block] 
						Case "cap"
							;[Block]
							ShouldPreventDropping = (I_268\Using = 1)
							;[End Block]
						Case "scp268"
							;[Block]
							ShouldPreventDropping = (I_268\Using = 2)
							;[End Block]
						Case "fine268"
							;[Block]
							ShouldPreventDropping = (I_268\Using = 3)
							;[End Block]
						Case "scp714"
							;[Block]
							ShouldPreventDropping = (I_714\Using = 2)
							;[End Block]
						Case "coarse714"
							;[Block]
							ShouldPreventDropping = (I_714\Using = 1)
							;[End Block]
						Case "scp427"
							;[Block]
							ShouldPreventDropping = I_427\Using
							;[End Block]
						Default
							;[Block]
							ShouldPreventDropping = False
							;[End Block]
					End Select
					If ShouldPreventDropping
						CreateHintMsg(GetLocalString("msg", "takeoff"))
					Else
						DropItem(SelectedItem)
						InvOpen = mo\MouseHit2
					EndIf
					
					If (Not mo\MouseHit2)
						MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
						StopMouseMovement()
					EndIf
				Else
					If Inventory(MouseSlot) = Null
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem
								Inventory(z) = Null
								Exit
							EndIf
						Next
						Inventory(MouseSlot) = SelectedItem
						SelectedItem = Null
					ElseIf Inventory(MouseSlot) <> SelectedItem
						PrevItem = Inventory(MouseSlot)
						
						Select SelectedItem\ItemTemplate\TempName
							Case "paper", "oldpaper", "origami", "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "playcard", "mastercard", "badge", "oldbadge", "ticket", "25ct", "coin", "key", "scp860", "scp714", "coarse714", "fine714", "ring", "scp500pill", "scp500pilldeath", "pill"
								;[Block]
								If Inventory(MouseSlot)\ItemTemplate\TempName = "clipboard"
									; ~ Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\ItemTemplate\TempName
									Local c%, ri%
									
									If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp714" And b <> "coarse714" And b <> "fine714" And b <> "ring" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "pill"
										For c = 0 To Inventory(MouseSlot)\InvSlots - 1
											If Inventory(MouseSlot)\SecondInv[c] = Null
												If SelectedItem <> Null
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\State = 1.0
													SetAnimTime(Inventory(MouseSlot)\Model, 0.0)
													Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
													
													For ri = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
															Exit
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null
													Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null
											CreateMsg(GetLocalString("msg", "clipboard.full"))
										Else
											If added\ItemTemplate\TempName = "paper" Lor added\ItemTemplate\TempName = "oldpaper"
												CreateMsg(GetLocalString("msg", "clipboard.paper"))
											ElseIf added\ItemTemplate\TempName = "badge" Lor added\ItemTemplate\TempName = "oldbadge"
												CreateMsg(Format(GetLocalString("msg", "clipboard.badge"), added\ItemTemplate\Name))
											Else
												CreateMsg(Format(GetLocalString("msg", "clipboard.add"), added\ItemTemplate\Name))
											EndIf
										EndIf
									Else
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
									EndIf
								ElseIf Inventory(MouseSlot)\ItemTemplate\TempName = "wallet"
									; ~ Add an item to wallet
									added.Items = Null
									b = SelectedItem\ItemTemplate\TempName
									If b <> "paper" And b <> "oldpaper" And b <> "origami"
										If (SelectedItem\ItemTemplate\TempName = "scp714" And I_714\Using = 2) Lor (SelectedItem\ItemTemplate\TempName = "coarse714" And I_714\Using = 1)
											CreateMsg(GetLocalString("msg", "takeoff"))
											SelectedItem = Null
											Return
										EndIf
										
										For c = 0 To Inventory(MouseSlot)\InvSlots - 1
											If Inventory(MouseSlot)\SecondInv[c] = Null
												Inventory(MouseSlot)\SecondInv[c] = SelectedItem
												Inventory(MouseSlot)\State = 1.0
												If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp714" And b <> "coarse714" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "pill" Then SetAnimTime(Inventory(MouseSlot)\Model, 3.0)
												Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
												
												For ri = 0 To MaxItemAmount - 1
													If Inventory(ri) = SelectedItem
														Inventory(ri) = Null
														PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
														Exit
													EndIf
												Next
												added = SelectedItem
												SelectedItem = Null
												Exit
											EndIf
										Next
										If SelectedItem <> Null
											CreateMsg(GetLocalString("msg", "wallet.full"))
										Else
											CreateMsg(Format(GetLocalString("msg", "wallet.add"), added\ItemTemplate\Name))
										EndIf
									Else
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
									EndIf
								Else
									For z = 0 To MaxItemAmount - 1
										If Inventory(z) = SelectedItem
											Inventory(z) = PrevItem
											Exit
										EndIf
									Next
									Inventory(MouseSlot) = SelectedItem
								EndIf
								SelectedItem = Null
								;[End Block]
							Case "coarsebat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(50.0)
										CreateMsg(GetLocalString("msg", "nav.bat"))
										;[End Block]
									Case "nav310"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.notfit"))
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.no"))
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(50.0)
										CreateMsg(GetLocalString("msg", "radio.bat"))
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.notfit"))
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.no"))
										;[End Block]
									Case "nvg"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(500.0)
										CreateMsg(GetLocalString("msg", "nvg.bat"))
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.no"))
										;[End Block]
									Case "veryfinenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.notfit"))
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(500.0)
										CreateMsg(GetLocalString("msg", "gear.bat"))
										;[End Block]
									Case "finescramble"
										;[Block]
										CreateMsg(GetLocalString("msg", "gear.bat.notfit"))
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "bat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(100.0)
										CreateMsg(GetLocalString("msg", "nav.bat"))
										;[End Block]
									Case "nav310"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.notfit"))
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.no"))
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(100.0)
										CreateMsg(GetLocalString("msg", "radio.bat"))
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.notfit"))
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.no"))
										;[End Block]
									Case "nvg"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 1000.0)
										CreateMsg(GetLocalString("msg", "nvg.bat"))
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.no"))
										;[End Block]
									Case "veryfinenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.notfit"))
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 1000.0)
										CreateMsg(GetLocalString("msg", "gear.bat"))
										;[End Block]
									Case "finescramble"
										;[Block]
										CreateMsg(GetLocalString("msg", "gear.bat.notfit"))
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "finebat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.notfit"))
										;[End Block]
									Case "nav310"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 200.0)
										CreateMsg(GetLocalString("msg", "nav.bat"))
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.no"))
										;[End Block]
									Case "radio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.notfit"))
										;[End Block]
									Case "18vradio"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 200.0)
										CreateMsg(GetLocalString("msg", "radio.bat"))
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.no"))
										;[End Block]
									Case "nvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.notfit"))
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.no"))
										;[End Block]
									Case "veryfinenvg"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(1000.0)
										CreateMsg(GetLocalString("msg", "nvg.bat"))
										;[End Block]
									Case "scramble"
										;[Block]
										CreateMsg(GetLocalString("msg", "gear.bat.notfit"))
										;[End Block]
									Case "finescramble"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(500.0, 1000.0)
										CreateMsg(GetLocalString("msg", "gear.bat"))
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "veryfinebat", "killbat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 1000.0)
										CreateMsg(GetLocalString("msg", "nav.bat"))
										;[End Block]
									Case "nav310"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.notfit"))
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg(GetLocalString("msg", "nav.bat.no"))
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(0.0, 1000.0)
										CreateMsg(GetLocalString("msg", "radio.bat"))
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.notfit"))
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg(GetLocalString("msg", "radio.bat.no"))
										;[End Block]
									Case "nvg"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(10000.0)
										CreateMsg(GetLocalString("msg", "nvg.bat"))
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.no"))
										;[End Block]
									Case "veryfinenvg"
										;[Block]
										CreateMsg(GetLocalString("msg", "nvg.bat.notfit"))
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(10000.0)
										CreateMsg(GetLocalString("msg", "gear.bat"))
										;[End Block]
									Case "finescramble"
										;[Block]
										CreateMsg(GetLocalString("msg", "gear.bat.notfit"))
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Default
								;[Block]
								For z = 0 To MaxItemAmount - 1
									If Inventory(z) = SelectedItem
										Inventory(z) = PrevItem
										Exit
									EndIf
								Next
								Inventory(MouseSlot) = SelectedItem
								SelectedItem = Null
								;[End Block]
						End Select
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		If (Not InvOpen) Then StopMouseMovement()
	Else
		If SelectedItem <> Null
			Select SelectedItem\ItemTemplate\TempName
				Case "gasmask", "finegasmask", "veryfinegasmask", "gasmask148"
					;[Block]
					If (Not PreventItemOverlapping(True))
						Select SelectedItem\ItemTemplate\TempName
							Case "gasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 1) Then Return
								;[End Block]
							Case "finegasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 2) Then Return
								;[End Block]
							Case "veryfinegasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 3) Then Return
								;[End Block]
							Case "gasmask148"
								;[Block]
								If IsDoubleItem(wi\GasMask, 4) Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						If SelectedItem\ItemTemplate\TempName <> "gasmask148"
							SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 1.5), 100.0)
						Else
							SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 2.0), 100.0)
						EndIf
						
						If SelectedItem\State = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If wi\GasMask > 0
								CreateMsg(GetLocalString("msg", "mask.off"))
								wi\GasMask = 0
							Else
								Select SelectedItem\ItemTemplate\TempName
									Case "gasmask"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on"))
										wi\GasMask = 1
										;[End Block]
									Case "finegasmask"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on.dry"))
										wi\GasMask = 2
										;[End Block]
									Case "veryfinegasmask"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on.easy"))
										wi\GasMask = 3
										;[End Block]
									Case "gasmask148"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on.easy"))
										wi\GasMask = 4
										;[End Block]
								End Select
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "fine1499"
					;[Block]
					If (Not PreventItemOverlapping(False, False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "scp1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 1) Then Return
								;[End Block]
							Case "fine1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 2) Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 1.6), 100.0)
						
						If SelectedItem\State = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If I_1499\Using > 0
								CreateMsg(GetLocalString("msg", "mask.off"))
								I_1499\Using = 0
							Else
								GiveAchievement(Achv1499)
								For r.Rooms = Each Rooms
									If r\RoomTemplate\RoomID = r_dimension_1499
										me\BlinkTimer = -1.0
										I_1499\PrevRoom = PlayerRoom
										I_1499\PrevX = EntityX(me\Collider)
										I_1499\PrevY = EntityY(me\Collider)
										I_1499\PrevZ = EntityZ(me\Collider)
										
										If I_1499\x = 0.0 And I_1499\y = 0.0 And I_1499\z = 0.0
											PositionEntity(me\Collider, r\x + 6086.0 * RoomScale, r\y + 304.0 * RoomScale, r\z + 2292.5 * RoomScale)
											RotateEntity(me\Collider, 0.0, 90.0, 0.0, True)
										Else
											PositionEntity(me\Collider, I_1499\x, I_1499\y + 0.05, I_1499\z)
										EndIf
										ResetEntity(me\Collider)
										TeleportToRoom(r)
										PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Enter.ogg"))
										I_1499\x = 0.0
										I_1499\y = 0.0
										I_1499\z = 0.0
										If n_I\Curr096 <> Null
											If n_I\Curr096\SoundCHN <> 0 Then SetStreamVolume_Strict(n_I\Curr096\SoundCHN, 0.0)
										EndIf
										For e.Events = Each Events
											If e\EventID = e_dimension_1499
												If EntityDistanceSquared(e\room\OBJ, me\Collider) > PowTwo(8300.0 * RoomScale)
													If e\EventState2 < 5.0 Then e\EventState2 = e\EventState2 + 1.0
												EndIf
												Exit
											EndIf
										Next
										Exit
									EndIf
								Next
								Select SelectedItem\ItemTemplate\TempName
									Case "scp1499"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on"))
										I_1499\Using = 1
										;[End Block]
									Case "fine1499"
										;[Block]
										CreateMsg(GetLocalString("msg", "mask.on.easy"))
										I_1499\Using = 2
										;[End Block]
								End Select
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "nvg", "veryfinenvg", "finenvg"
					;[Block]
					If (Not PreventItemOverlapping(False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "nvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 1) Then Return
								;[End Block]
							Case "veryfinenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 2) Then Return
								;[End Block]
							Case "finenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 3) Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State3 = Min(SelectedItem\State3 + (fps\Factor[0] / 1.5), 100.0)
						
						If SelectedItem\State3 = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If wi\NightVision > 0
								CreateMsg(GetLocalString("msg", "nvg.off"))
								opt\CameraFogFar = 6.0
								wi\NightVision = 0
								If SelectedItem\State > 0.0 Then PlaySound_Strict(NVGSFX[1])
							Else
								CreateMsg(GetLocalString("msg", "nvg.on"))
								opt\CameraFogFar = 17.0
								Select SelectedItem\ItemTemplate\TempName
									Case "nvg"
										;[Block]
										wi\NightVision = 1
										;[End Block]
									Case "veryfinenvg"
										;[Block]
										wi\NightVision = 2
										;[End Block]
									Case "finenvg"
										;[Block]
										wi\NightVision = 3
										;[End Block]
								End Select
								If SelectedItem\State > 0.0 Then PlaySound_Strict(NVGSFX[0])
							EndIf
							SelectedItem\State3 = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "scramble", "finescramble"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "scramble"
								;[Block]
								If IsDoubleItem(wi\SCRAMBLE, 1) Then Return
								;[End Block]
							Case "finescramble"
								;[Block]
								If IsDoubleItem(wi\SCRAMBLE, 2) Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State3 = Min(SelectedItem\State3 + (fps\Factor[0] / 1.5), 100.0)
						
						If SelectedItem\State3 = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If wi\SCRAMBLE > 0
								CreateMsg(GetLocalString("msg", "gear.off"))
								opt\CameraFogFar = 6.0
								wi\SCRAMBLE = 0
							Else
								CreateMsg(GetLocalString("msg", "gear.on"))
								opt\CameraFogFar = 9.0
								Select SelectedItem\ItemTemplate\TempName
									Case "scramble"
										;[Block]
										wi\SCRAMBLE = 1
										;[End Block]
									Case "finescramble"
										;[Block]
										wi\SCRAMBLE = 2
										;[End Block]
								End Select
							EndIf
							SelectedItem\State3 = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "helmet"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, True))
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fps\Factor[0], 100.0)
						
						If SelectedItem\State = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If wi\BallisticHelmet
								CreateMsg(GetLocalString("msg", "helmet.off"))
								wi\BallisticHelmet = False
							Else
								CreateMsg(GetLocalString("msg", "helmet.on"))
								wi\BallisticHelmet = True
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "cap", "scp268", "fine268"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, False, False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "cap"
								;[Block]
								If IsDoubleItem(I_268\Using, 1) Then Return
								;[End Block]
							Case "scp268"
								;[Block]
								If IsDoubleItem(I_268\Using, 2) Then Return
								;[End Block]
							Case "fine268"
								;[Block]
								If IsDoubleItem(I_268\Using, 3) Then Return
								;[End Block]
						End Select
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fps\Factor[0], 100.0)
						
						If SelectedItem\State = 100.0
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							
							If I_268\Using > 0
								If I_268\Using > 1 Then PlaySound_Strict(LoadTempSound("SFX\SCP\268\InvisibilityOff.ogg"))
								CreateMsg(GetLocalString("msg", "cap.off"))
								I_268\Using = 0
							Else
								GiveAchievement(Achv268)
								CreateMsg(GetLocalString("msg", "cap.on"))
								Select SelectedItem\ItemTemplate\TempName
									Case "cap"
										;[Block]
										I_268\Using = 1
										;[End Block]
									Case "scp268"
										;[Block]
										I_268\Using = 2
										;[End Block]
									Case "fine268"
										;[Block]
										I_268\Using = 3
										;[End Block]
								End Select
								If I_268\Using > 1 Then PlaySound_Strict(LoadTempSound("SFX\SCP\268\InvisibilityOn.ogg"))
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
					
					SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / (2.0 + (0.5 * (SelectedItem\ItemTemplate\TempName = "finevest")))), 100)
					
					If SelectedItem\State = 100.0
						If wi\BallisticVest > 0
							CreateMsg(GetLocalString("msg", "vest.off"))
							wi\BallisticVest = 0
							DropItem(SelectedItem)
						Else
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							Select SelectedItem\ItemTemplate\TempName
								Case "vest"
									;[Block]
									CreateMsg(GetLocalString("msg", "vest.on.slight"))
									wi\BallisticVest = 1
									;[End Block]
								Case "finevest"
									;[Block]
									CreateMsg(GetLocalString("msg", "vest.on.heavy"))
									wi\BallisticVest = 2
									;[End Block]
							End Select
						EndIf
						SelectedItem\State = 0.0
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "hazmatsuit", "finehazmatsuit", "veryfinehazmatsuit", "hazmatsuit148"
					;[Block]
					me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
					
					If SelectedItem\ItemTemplate\TempName <> "hazmatsuit148"
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 4.0), 100.0)
					Else
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 5.0), 100.0)
					EndIf
					
					If SelectedItem\State = 100.0
						If wi\HazmatSuit > 0
							CreateMsg(GetLocalString("msg", "suit.off"))
							wi\HazmatSuit = 0
							DropItem(SelectedItem)
						Else
							If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
							If wi\NightVision > 0 Then opt\CameraFogFar = 6.0 : wi\NightVision = 0
							If wi\SCRAMBLE > 0 Then opt\CameraFogFar = 6.0 : wi\SCRAMBLE = 0
							wi\GasMask = 0 : wi\BallisticHelmet = False
							I_427\Using = False : I_1499\Using = 0
							I_268\Using = 0
							Select SelectedItem\ItemTemplate\TempName
								Case "hazmatsuit"
									;[Block]
									CreateMsg(GetLocalString("msg", "suit.on"))
									wi\HazmatSuit = 1
									;[End Block]
								Case "finehazmatsuit"
									;[Block]
									CreateMsg(GetLocalString("msg", "suit.dry"))
									wi\HazmatSuit = 2
									;[End Block]
								Case "veryfinehazmatsuit"
									;[Block]
									CreateMsg(GetLocalString("msg", "suit.on.easy"))
									wi\HazmatSuit = 3
									;[End Block]
								Case "hazmatsuit148"
									;[Block]
									CreateMsg(GetLocalString("msg", "suit.on.easy"))
									wi\HazmatSuit = 4
									;[End Block]
							End Select
						EndIf
						SelectedItem\State = 0.0
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell.ogg"))
					
					GiveAchievement(Achv513)
					
					If n_I\Curr513_1 = Null And (Not me\Deaf) Then n_I\Curr513_1 = CreateNPC(NPCType513_1, 0.0, 0.0, 0.0)
					SelectedItem = Null
					;[End Block]
				Case "scp500pill"
					;[Block]
					If CanUseItem(True)
						GiveAchievement(Achv500)
						
						If I_008\Timer > 0.0
							CreateMsg(GetLocalString("msg", "pill.nausea"))
							I_008\Revert = True
						ElseIf I_409\Timer > 0.0
							CreateMsg(GetLocalString("msg", "pill.crystals"))
							I_409\Revert = True
						Else
							CreateMsg(GetLocalString("msg", "pill"))
						EndIf
						
						me\DeathTimer = 0.0
						me\Stamina = 100.0
						
						For i = 0 To 6
							I_1025\State[i] = 0.0
						Next
						
						If me\StaminaEffect > 1.0
							me\StaminaEffect = 1.0
							me\StaminaEffectTimer = 0.0
						EndIf
						
						If me\BlinkEffect > 1.0
							me\BlinkEffect = 1.0
							me\BlinkEffectTimer = 0.0
						EndIf
						
						For e.Events = Each Events
							If e\EventID = e_1048_a
								If e\EventState2 > 0.0
									CreateMsg(GetLocalString("msg", "pill.ears"))
									e\EventState3 = 1.0
								EndIf
							EndIf
						Next
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If CanUseItem(True)
						Select Rand(5)
							Case 1
								;[Block]
								me\Injuries = 3.5
								CreateMsg(GetLocalString("msg", "bleed"))
								;[End Block]
							Case 2
								;[Block]
								me\Injuries = 0.0
								me\Bloodloss = 0.0
								CreateMsg(GetLocalString("msg", "rapidly"))
								;[End Block]
							Case 3
								;[Block]
								me\Injuries = Max(0.0, me\Injuries - Rnd(0.5, 3.5))
								me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 100.0))
								CreateMsg(GetLocalString("msg", "better_1"))
								;[End Block]
							Case 4
								;[Block]
								me\BlurTimer = 10000.0
								me\Bloodloss = 0.0
								CreateMsg(GetLocalString("msg", "nausea"))
								;[End Block]
							Case 5
								;[Block]
								me\BlinkTimer = -10.0
								
								If RID = r_dimension_1499 Lor IsPlayerOutsideFacility()
									me\Injuries = 2.5
									CreateMsg(GetLocalString("msg", "bleed"))
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\RoomID = r_dimension_106
											TeleportToRoom(r)
											TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.5, EntityZ(r\OBJ))
											PlaySound_Strict(Use914SFX)
											me\DropSpeed = 0.0
											n_I\Curr106\State = -2500.0
											Exit
										EndIf
									Next
									CreateMsg(GetLocalString("msg", "aid.106"))
								EndIf
								;[End Block]
						End Select
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If CanUseItem(True, True)
						If me\Bloodloss = 0.0 And me\Injuries = 0.0
							CreateMsg(GetLocalString("msg", "aid.no"))
							SelectedItem = Null
							Return
						Else
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
							If (Not me\Crouch) Then SetCrouch(True)
							
							If SelectedItem\ItemTemplate\TempName = "firstaid"
								SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 5.0), 100.0)
							Else
								SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 4.0), 100.0)
							EndIf
							
							If SelectedItem\State = 100.0
								If SelectedItem\ItemTemplate\TempName = "finefirstaid"
									me\Bloodloss = 0.0
									me\Injuries = Max(0.0, me\Injuries - 2.0)
									If me\Injuries = 0.0
										CreateMsg(GetLocalString("msg", "aid.fine"))
									ElseIf me\Injuries > 1.0
										CreateMsg(GetLocalString("msg", "aid.bleed"))
									Else
										CreateMsg(GetLocalString("msg", "aid.sore"))
									EndIf
								Else
									me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 20.0))
									If me\Injuries >= 2.5
										CreateMsg(GetLocalString("msg", "aid.toobad_1"))
										me\Injuries = Max(2.5, me\Injuries - Rnd(0.3, 0.7))
									ElseIf me\Injuries > 1.0
										me\Injuries = Max(0.5, me\Injuries - Rnd(0.5, 1.0))
										If me\Injuries > 1.0
											CreateMsg(GetLocalString("msg", "aid.toobad_2"))
										Else
											CreateMsg(GetLocalString("msg", "aid.stop"))
										EndIf
									Else
										If me\Injuries > 0.5
											me\Injuries = 0.5
											CreateMsg(GetLocalString("msg", "aid.slight"))
										Else
											me\Injuries = me\Injuries / 2.0
											CreateMsg(GetLocalString("msg", "aid.nowalk"))
										EndIf
									EndIf
									
									If SelectedItem\ItemTemplate\TempName = "firstaid2"
										Select Rand(6)
											Case 1
												;[Block]
												chs\SuperMan = True
												CreateMsg(GetLocalString("msg", "aid.super"))
												;[End Block]
											Case 2
												;[Block]
												opt\InvertMouseX = (Not opt\InvertMouseX)
												opt\InvertMouseY = (Not opt\InvertMouseY)
												CreateMsg(GetLocalString("msg", "aid.invert"))
												;[End Block]
											Case 3
												;[Block]
												me\BlurTimer = 5000.0
												CreateMsg(GetLocalString("msg", "nausea"))
												;[End Block]
											Case 4
												;[Block]
												me\BlinkEffect = 0.6
												me\BlinkEffectTimer = Rnd(20.0, 30.0)
												;[End Block]
											Case 5
												;[Block]
												me\Bloodloss = 0.0
												me\Injuries = 0.0
												CreateMsg(GetLocalString("msg", "aid.stopall"))
												;[End Block]
											Case 6
												;[Block]
												CreateMsg(GetLocalString("msg", "aid.through"))
												me\Injuries = 3.5
												;[End Block]
										End Select
									EndIf
								EndIf
								RemoveItem(SelectedItem)
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops", "eyedrops2"
					;[Block]
					If CanUseItem()
						me\BlinkEffect = 0.7
						me\BlinkEffectTimer = Rnd(25.0, 35.0)
						me\BlurTimer = 200.0
						
						CreateMsg(GetLocalString("msg", "eyedrop.moisturized"))
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If CanUseItem()
						me\BlinkEffect = 0.5
						me\BlinkEffectTimer = Rnd(35.0, 45.0)
						me\Bloodloss = Max(me\Bloodloss - 1.0, 0.0)
						me\BlurTimer = 200.0
						
						CreateMsg(GetLocalString("msg", "eyedrop.moisturized.very"))
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem()
						me\BlinkEffect = 0.0
						me\BlinkEffectTimer = 60.0
						me\EyeStuck = 10000.0
						me\BlurTimer = 1000.0
						
						CreateMsg(GetLocalString("msg", "eyedrop.moisturized.veryvery"))
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025)
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(ItemHUDTexturePath + "page_1025(" + (Int(SelectedItem\State) + 1) + ").png")
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\State3 = 0.0
						If I_714\Using = 0 And wi\GasMask <> 4 And wi\HazmatSuit <> 4
							If SelectedItem\State = 7.0
								If I_008\Timer = 0.0 Then I_008\Timer = 0.001
							Else
								I_1025\State[SelectedItem\State] = Max(1.0, I_1025\State[SelectedItem\State])
								I_1025\State[7] = 1 + (SelectedItem\State2 = 2.0) * 2.0 ; ~ 3x as fast if VERYFINE
							EndIf
						EndIf
						If Rand(3 - (SelectedItem\State2 <> 2.0) * SelectedItem\State2) = 1 ; ~ Higher chance for good illness if FINE, lower change for good illness if COARSE
							SelectedItem\State = 6.0
						Else
							SelectedItem\State = Rand(0, 7)
						EndIf
						SelectedItem\State3 = 1.0
					EndIf
					;[End Block]
				Case "book"
					;[Block]
					CreateMsg(GetLocalString("msg", "redbook"))
					SelectedItem = Null
					;[End Block]
				Case "cup"
					;[Block]
					If CanUseItem(True)
						Local Drink$ = Trim(SelectedItem\Name)
						
						If Lower(Left(Drink, Min(6, Len(Drink)))) = "cup of"
							Drink = Right(Drink, Len(Drink) - 7)
						ElseIf Lower(Left(Drink, Min(8, Len(Drink)))) = "a cup of"
							Drink = Right(Drink, Len(Drink) - 9)
						EndIf
						
						StrTemp = GetFileLocalString(SCP294File, Drink, "Message", "", False)
						If StrTemp <> "" Then CreateMsg(StrTemp)
						
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Lethal", "", False))
							msg\DeathMsg = GetFileLocalString(SCP294File, Drink, "Death Message", "", False)
							Kill()
						EndIf
						me\BlurTimer = Max(Int(GetFileLocalString(SCP294File, Drink, "Blur", "", False)) * 70.0, 0.0)
						If me\VomitTimer = 0.0
							me\VomitTimer = Int(GetFileLocalString(SCP294File, Drink, "Vomit", "", False))
						Else
							me\VomitTimer = Min(me\VomitTimer, Int(GetFileLocalString(SCP294File, Drink, "Vomit", "", False)))
						EndIf
						me\CameraShakeTimer = GetFileLocalString(SCP294File, Drink, "Camera Shake", "", False)
						me\DeafTimer = Max(me\DeafTimer + Int(GetFileLocalString(SCP294File, Drink, "Deaf Timer", "", False)), 0.0)
						me\Injuries = Max(me\Injuries + Float(GetFileLocalString(SCP294File, Drink, "Damage", "", False)), 0.0)
						me\Bloodloss = Max(me\Bloodloss + Int(GetFileLocalString(SCP294File, Drink, "Blood Loss", "", False)), 0.0)
						StrTemp = GetFileLocalString(SCP294File, Drink, "Sound", "", False)
						If StrTemp <> "" Then PlaySound_Strict(LoadTempSound(StrTemp), True)
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Stomach Ache", "", False)) Then I_1025\State[3] = 1.0
						
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Infection", "", False)) Then I_008\Timer = I_008\Timer + 0.001
						
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Crystallization", "", False)) Then I_409\Timer = I_409\Timer + 0.001
						
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Mutation", "", False))
							If I_427\Timer < 70.0 * 360.0 Then I_427\Timer = 70.0 * 360.0
						EndIf
						
						If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Revitalize", "", False))
							For i = 0 To 6
								I_1025\State[i] = 0.0
							Next
						EndIf
						
						If me\DeathTimer = 0.0
							me\DeathTimer = Int(GetFileLocalString(SCP294File, Drink, "Death Timer", "", False)) * 70.0
						Else
							me\DeathTimer = Min(me\DeathTimer, Int(GetFileLocalString(SCP294File, Drink, "Death Timer", "", False)) * 70.0)
						EndIf
						
						; ~ The state of refined items is more than 1.0 (fine setting increases it by 1, very fine doubles it)
						StrTemp = GetFileLocalString(SCP294File, Drink, "Blink Effect", "", False)
						If StrTemp <> "" Then me\BlinkEffect = Float(StrTemp) ^ SelectedItem\State
						StrTemp = GetFileLocalString(SCP294File, Drink, "Blink Effect Timer", "", False)
						If StrTemp <> "" Then me\BlinkEffectTimer = Float(StrTemp) * SelectedItem\State
						StrTemp = GetFileLocalString(SCP294File, Drink, "Stamina Effect", "", False)
						If StrTemp <> "" Then me\StaminaEffect = Float(StrTemp) ^ SelectedItem\State
						StrTemp = GetFileLocalString(SCP294File, Drink, "Stamina Effect Timer", "", False)
						If StrTemp <> "" Then me\StaminaEffectTimer = Float(StrTemp) * SelectedItem\State
						StrTemp = GetFileLocalString(SCP294File, Drink, "Refuse Message", "", False)
						If StrTemp <> ""
							CreateMsg(StrTemp)
							SelectedItem = Null
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0.0, 0.0, 0.0)
							it\Picked = True
							For i = 0 To MaxItemAmount - 1
								If Inventory(i) = SelectedItem
									Inventory(i) = it
									Exit
								EndIf
							Next
							EntityType(it\Collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)
						EndIf
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					If CanUseItem(True, True)
						me\HealTimer = 30.0
						me\StaminaEffect = 0.5
						me\StaminaEffectTimer = 20.0
						
						CreateMsg(GetLocalString("msg", "syringe_1"))
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "finesyringe"
					;[Block]
					If CanUseItem(True, True)
						me\HealTimer = Rnd(20.0, 40.0)
						me\StaminaEffect = Rnd(0.4, 0.6)
						me\StaminaEffectTimer = Rnd(20.0, 30.0)
						
						CreateMsg(GetLocalString("msg", "syringe_2"))
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					If CanUseItem(True, True)
						Select Rand(3)
							Case 1
								;[Block]
								me\HealTimer = Rnd(40.0, 60.0)
								me\StaminaEffect = 0.1
								me\StaminaEffectTimer = 30.0
								CreateMsg(GetLocalString("msg", "syringe_3"))
								;[End Block]
							Case 2
								;[Block]
								chs\SuperMan = True
								CreateMsg(GetLocalString("msg", "syringe_4"))
								;[End Block]
							Case 3
								;[Block]
								me\VomitTimer = 30.0
								CreateMsg(GetLocalString("msg", "syringe_5"))
								;[End Block]
						End Select
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "syringeinf"
					;[Block]
					If CanUseItem(True, True)
						CreateMsg(GetLocalString("msg", "syringe_6"))
						
						me\VomitTimer = 70.0 * 1.0
						
						I_008\Timer = I_008\Timer + (1.0 + (1.0 * SelectedDifficulty\AggressiveNPCs))
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img)
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img)
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\ItemTemplate\TempName <> "fineradio" And SelectedItem\ItemTemplate\TempName <> "veryfineradio" Then SelectedItem\State = Max(0.0, SelectedItem\State - fps\Factor[0] * 0.004)
					
					; ~ RadioState[5] = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState[6] = A timer for the "code channel"
					; ~ RadioState[7] = Another timer for the "code channel"
					
					If SelectedItem\State > 0.0 Lor (SelectedItem\ItemTemplate\TempName = "fineradio" Lor SelectedItem\ItemTemplate\TempName = "veryfineradio")
						IsUsingRadio = True
						If RadioState[5] = 0.0
							CreateMsg(GetLocalString("msg", "radio"))
							RadioState[5] = 1.0
							RadioState[0] = -1.0
						EndIf
						
						If RID = r_dimension_106
							For i = 0 To 5
								If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
							Next
							
							If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
						ElseIf CoffinDistance < 8.0
							For i = 0 To 5
								If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
							Next
							
							If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic895)
						Else
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									If (Not opt\EnableUserTracks)
										If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
									ElseIf UserTrackMusicAmount < 1
										If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
									Else
										If ChannelPlaying(RadioCHN[6]) Then StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
										
										If (Not ChannelPlaying(RadioCHN[0]))
											If (Not UserTrackFlag)
												If opt\UserTrackMode
													If RadioState[0] < (UserTrackMusicAmount - 1)
														RadioState[0] = RadioState[0] + 1.0
													Else
														RadioState[0] = 0.0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0.0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										Else
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2)
											PlaySound_Strict(RadioSquelch)
											If (Not UserTrackFlag)
												If opt\UserTrackMode
													If RadioState[0] < (UserTrackMusicAmount - 1)
														RadioState[0] = RadioState[0] + 1.0
													Else
														RadioState[0] = 0.0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0.0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										EndIf
									EndIf
									;[End Block]
								Case 1
									;[Block]
									If ChannelPlaying(RadioCHN[6]) Then StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
									
									If (Not ChannelPlaying(RadioCHN[1]))
										If RadioState[1] >= 5.0
											RadioCHN[1] = PlaySound_Strict(RadioSFX(0, 1))
											RadioState[1] = 0.0
										Else
											RadioState[1] = RadioState[1] + 1.0
											RadioCHN[1] = PlaySound_Strict(RadioSFX(0, 0))
										EndIf
									EndIf
									;[End Block]
								Case 2
									;[Block]
									If ChannelPlaying(RadioCHN[6]) Then StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
									
									If (Not ChannelPlaying(RadioCHN[2]))
										RadioState[2] = RadioState[2] + 1.0
										If RadioState[2] = 17.0 Then RadioState[2] = 1.0
										If Floor(RadioState[2] / 2.0) = Ceil(RadioState[2] / 2.0)
											RadioCHN[2] = PlaySound_Strict(RadioSFX(1, Int(RadioState[2] / 2.0)))
										Else
											RadioCHN[2] = PlaySound_Strict(RadioSFX(1, 0))
										EndIf
									EndIf
									;[End Block]
								Case 3
									;[Block]
									If (Not ChannelPlaying(RadioCHN[6])) And (Not ChannelPlaying(RadioCHN[3])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
									
									If MTFTimer > 0.0
										If (Not RadioState2[6]) Then RadioState[3] = RadioState[3] + Max(Rand(-10, 1), 0.0)
										Select RadioState[3]
											Case 40
												;[Block]
												If (Not RadioState2[0])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[0] = True
												EndIf
												;[End Block]
											Case 400
												;[Block]
												If (Not RadioState2[1])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[1] = True
												EndIf
												;[End Block]
											Case 800
												;[Block]
												If (Not RadioState2[2])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[2] = True
												EndIf
												;[End Block]
											Case 1200
												;[Block]
												If (Not RadioState2[3])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[3] = True
												EndIf
												;[End Block]
											Case 1600
												;[Block]
												If (Not RadioState2[4])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[4] = True
												EndIf
												;[End Block]
											Case 2000
												;[Block]
												If (Not RadioState2[5])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[5] = True
												EndIf
												;[End Block]
											Case 2400
												;[Block]
												If (Not RadioState2[6])
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"), True)
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[6] = True
												EndIf
												;[End Block]
										End Select
									EndIf
									;[End Block]
								Case 4
									;[Block]
									If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
									
									If (Not ChannelPlaying(RadioCHN[4]))
										If (Not RemoteDoorOn) And RadioState[8] = 0
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter3.ogg"), True)
											RadioState[8] = 1
										Else
											RadioState[4] = RadioState[4] + Max(Rand(-10, 1), 0.0)
											
											Select RadioState[4]
												Case 10
													;[Block]
													If (Not n_I\Curr106\Contained)
														If (Not RadioState3[0])
															RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\OhGod.ogg"), True)
															RadioState[4] = RadioState[4] + 1.0
															RadioState3[0] = True
														EndIf
													EndIf
													;[End Block]
												Case 100
													;[Block]
													If (Not RadioState3[1])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter2.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[1] = True
													EndIf
													;[End Block]
												Case 158
													;[Block]
													If MTFTimer = 0.0 And (Not RadioState3[2])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin1.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState[2] = True
													EndIf
													;[End Block]
												Case 200
													;[Block]
													If (Not RadioState3[3])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter4.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[3] = True
													EndIf
													;[End Block]
												Case 260
													;[Block]
													If (Not RadioState3[4])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[4] = True
													EndIf
													;[End Block]
												Case 300
													;[Block]
													If (Not RadioState3[5])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter1.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[5] = True
													EndIf
													;[End Block]
												Case 350
													;[Block]
													If (Not RadioState3[6])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin2.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[6] = True
													EndIf
													;[End Block]
												Case 400
													;[Block]
													If (Not RadioState3[7])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[7] = True
													EndIf
													;[End Block]
												Case 450
													;[Block]
													If (Not RadioState3[8])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin3.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[8] = True
													EndIf
													;[End Block]
												Case 600
													;[Block]
													If (Not RadioState3[9])
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin4.ogg"), True)
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[9] = True
													EndIf
													;[End Block]
											End Select
										EndIf
									EndIf
									;[End Block]
								Case 5
									;[Block]
									If ChannelPlaying(RadioCHN[6]) Then StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
									If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
									;[End Block]
							End Select
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio"
								SelectedItem\State2 = -1
								If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)
								RadioState[6] = RadioState[6] + fps\Factor[0]
								Temp = Mid(Str(CODE_DR_MAYNARD), RadioState[8] + 1.0, 1)
								If RadioState[6] - fps\Factor[0] <= RadioState[7] * 50.0 And RadioState[6] > RadioState[7] * 50.0
									PlaySound_Strict(RadioBuzz)
									RadioState[7] = RadioState[7] + 1.0
									If RadioState[7] >= Temp
										RadioState[7] = 0.0
										RadioState[6] = -100.0
										RadioState[8] = RadioState[8] + 1.0
										If RadioState[8] = 4.0 Then RadioState[8] = 0.0 : RadioState[6] = -200.0
									EndIf
								EndIf
							Else
								For i = 2 To 6
									If KeyHit(i)
										If SelectedItem\State2 <> i - 2
											PlaySound_Strict(RadioSquelch)
											PauseChannel(RadioCHN[Int(SelectedItem\State2)])
										EndIf
										SelectedItem\State2 = i - 2
										StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
									EndIf
								Next
								If (Not ChannelPlaying(RadioCHN[SelectedItem\State2])) Then ResumeChannel(RadioCHN[SelectedItem\State2])
							EndIf
						EndIf
						
						If SelectedItem\ItemTemplate\TempName = "radio" Lor SelectedItem\ItemTemplate\TempName = "18vradio"
							If SelectedItem\State <= 20.0
								UpdateBatteryTimer()
								If BatMsgTimer >= 70.0 * 1.0
									If (Not ChannelPlaying(LowBatteryCHN[0])) Then LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
								EndIf
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "nav", "nav310", "navulti", "nav300"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\ItemTemplate\Name = "nav" Lor SelectedItem\ItemTemplate\Name = "nav310"
						SelectedItem\State = Max(0.0, SelectedItem\State - fps\Factor[0] * 0.005)
						
						If SelectedItem\State > 0.0 And SelectedItem\State <= 20.0
							UpdateBatteryTimer()
							If BatMsgTimer >= 70.0 * 1.0
								If (Not ChannelPlaying(LowBatteryCHN[0])) Then LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "cigarette"
					;[Block]
					If CanUseItem(True)
						Select Rand(6)
							Case 1
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_1"))
								;[End Block]
							Case 2
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_2"))
								;[End Block]
							Case 3
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_3"))
								;[End Block]
							Case 4
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_4"))
								;[End Block]
							Case 5
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_5"))
								;[End Block]
							Case 6
								;[Block]
								CreateMsg(GetLocalString("msg", "cigarette_6"))
								;[End Block]
						End Select
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp420j"
					;[Block]
					If CanUseItem(True)
						If I_714\Using > 0
							CreateMsg(GetLocalString("msg", "420j.no"))
						Else
							CreateMsg(GetLocalString("msg", "420j.yeah"))
							me\Injuries = Max(me\Injuries - 0.5, 0.0)
							me\BlurTimer = 500.0
							GiveAchievement(Achv420_J)
							PlaySound_Strict(LoadTempSound("SFX\Music\Using420J.ogg"))
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "joint", "scp420s"
					;[Block]
					If CanUseItem(True)
						If I_714\Using > 0
							CreateMsg(GetLocalString("msg", "420j.no"))
						Else
							CreateMsg(GetLocalString("msg", "420j.dead"))
							msg\DeathMsg = Format(GetLocalString("death", "joint"), SubjectName)
							Kill()
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714", "coarse714"
					;[Block]
					If CanUseItem(True, True)
						Select SelectedItem\ItemTemplate\TempName
							Case "coarse714"
								;[Block]
								If IsDoubleItem(I_714\Using, 1) Then Return
								;[End Block]
							Case "scp714"
								;[Block]
								If IsDoubleItem(I_714\Using, 2) Then Return
								;[End Block]
						End Select
						
						If I_714\Using > 0
							CreateMsg(GetLocalString("msg", "714.off"))
							I_714\Using = 0
						Else
							GiveAchievement(Achv714)
							CreateMsg(GetLocalString("msg", "714.on"))
							Select SelectedItem\ItemTemplate\TempName
								Case "coarse714"
									;[Block]
									I_714\Using = 1
									;[End Block]
								Case "scp714"
									;[Block]
									I_714\Using = 2
									;[End Block]
							End Select
						EndIf
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "fine714", "ring"
					;[Block]
					If CanUseItem(True, True)
						If SelectedItem\ItemTemplate\TempName = "fine714"
							CreateMsg(GetLocalString("msg", "714.sleep"))
							msg\DeathMsg = Format(GetLocalString("death", "ringsleep"), SubjectName)
							Kill()
						Else
							CreateMsg(GetLocalString("msg", "714.small"))
						EndIf
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "ticket"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\State = 0.0
						CreateMsg(GetLocalString("msg", "ticket"))
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(5) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
					EndIf
					
					If SelectedItem\State = 0.0
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "oldbadge"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\State = 0.0
						CreateMsg(GetLocalString("msg", "oldbadge"))
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
					EndIf
					
					If SelectedItem\State = 0.0
						me\BlurTimer = 1000.0
						CreateMsg(GetLocalString("msg", "oldpaper"))
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\State = 0.0
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						CreateMsg(GetLocalString("msg", "lostkey"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\State = 0.0
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(5) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using
						CreateMsg(GetLocalString("msg", "427.off"))
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						CreateMsg(GetLocalString("msg", "427.on"))
						I_427\Using = True
					EndIf
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(True)
						CreateMsg(GetLocalString("msg", "pill"))
						I_1025\State[0] = 0.0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp500pilldeath"
					;[Block]
					If CanUseItem(True)
						CreateMsg(GetLocalString("msg", "pill"))
						
						If I_427\Timer < 70.0 * 360.0 Then I_427\Timer = 70.0 * 360.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp500"
					;[Block]
					If I_500\Taken < Rand(20)
						If ItemAmount < MaxItemAmount
							For i = 0 To MaxItemAmount - 1
								If Inventory(i) = Null
									Inventory(i) = CreateItem("SCP-500-01", "scp500pill", 0.0, 0.0, 0.0)
									Inventory(i)\Picked = True
									Inventory(i)\Dropped = -1
									Inventory(i)\ItemTemplate\Found = True
									HideEntity(Inventory(i)\Collider)
									EntityType(Inventory(i)\Collider, HIT_ITEM)
									EntityParent(Inventory(i)\Collider, 0)
									Exit
								EndIf
							Next
							CreateMsg(GetLocalString("msg", "500"))
							I_500\Taken = I_500\Taken + 1
						Else
							CreateMsg(GetLocalString("msg", "cantcarry"))
						EndIf
						SelectedItem = Null
					Else
						I_500\Taken = 0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp1123"
					;[Block]
					Use1123()
					SelectedItem = Null
					;[End Block]
				Case "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "hand3", "25ct", "scp005", "key", "coin", "mastercard", "paper"
					;[Block]
					; ~ Skip this line
					;[End Block]
				Default
					;[Block]
					; ~ Check if the item is an inventory-type object
					If SelectedItem\InvSlots > 0 Then OtherOpen = SelectedItem
					ResetInput()
					SelectedItem = Null
					;[End Block]
			End Select
			
			If ((mo\MouseHit2 Lor KeyHit(key\INVENTORY)) And (Not MenuOpen)) Lor me\Terminated Lor me\FallTimer < 0.0 Lor (Not me\Playable) Lor me\Zombie
				Select SelectedItem\ItemTemplate\TempName
					Case "firstaid", "finefirstaid", "firstaid2", "cap", "scp268", "fine268", "scp1499", "fine1499", "gasmask", "finegasmask", "veryfinegasmask", "gasmask148", "helmet"
						;[Block]
						SelectedItem\State = 0.0
						;[End Block]
					Case "vest", "finevest"
						;[Block]
						SelectedItem\State = 0.0
						If wi\BallisticVest = 0 Then DropItem(SelectedItem, False)
						;[End Block]
					Case "hazmatsuit", "finehazmatsuit", "veryfinehazmatsuit", "hazmatsuit148"
						;[Block]
						SelectedItem\State = 0.0
						If wi\HazmatSuit = 0 Then DropItem(SelectedItem, False)
						;[End Block]
					Case "nvg", "veryfinenvg", "finenvg", "scramble", "finescramble", "scp1025"
						;[Block]
						SelectedItem\State3 = 0.0
						;[End Block]
				End Select
				If SelectedItem\ItemTemplate\SoundID <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\SoundID])
				If SelectedItem\ItemTemplate\Img <> 0 Then FreeImage(SelectedItem\ItemTemplate\Img) : SelectedItem\ItemTemplate\Img = 0
				
				For i = 0 To 6
					If ChannelPlaying(RadioCHN[i]) Then StopChannel(RadioCHN[i]) : RadioCHN[i] = 0
				Next
				IsUsingRadio = False
				
				SelectedItem = Null
			EndIf
		Else
			If ChannelPlaying(LowBatteryCHN[0]) Then StopChannel(LowBatteryCHN[0]) : LowBatteryCHN[0] = 0
		EndIf
	EndIf
	
	For it.Items = Each Items
		If it <> SelectedItem
			Select it\ItemTemplate\TempName
				Case "firstaid", "finefirstaid", "firstaid2", "vest", "finevest", "hazmatsuit", "finehazmatsuit", "veryfinehazmatsuit", "hazmatsuit148", "cap", "scp268", "fine268", "scp1499", "fine1499", "gasmask", "finegasmask", "veryfinegasmask", "gasmask148", "helmet"
					;[Block]
					it\State = 0.0
					;[End Block]
				Case "nvg", "veryfinenvg", "finenvg", "scramble", "finescramble", "scp1025"
					;[Block]
					it\State3 = 0.0
					;[End Block]
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	CatchErrors("Uncaught: UpdateGUI()")
End Function

Function RenderHUD%()
	If me\Terminated Lor me\FallTimer < 0.0 Lor (Not me\Playable) Then Return
	
	Local x%, y%, Width%, Height%, WalkIconID%, BlinkIconID%
	Local i%
	Local PlayerPosY# = EntityY(me\Collider)
	Local IconColoredRectSize% = 36 * MenuScale
	Local IconColoredRectSpaceX% = 53 * MenuScale
	Local IconColoredRectSpaceY% = 3 * MenuScale
	Local IconRectSize% = 32 * MenuScale
	Local IconRectSpace% = 51 * MenuScale
	Local IconSpace% = 50 * MenuScale
	Local ySpace% = 40 * MenuScale
	
	Width = 200 * MenuScale
	Height = 20 * MenuScale
	x = 80 * MenuScale
	y = opt\GraphicHeight - (15 * MenuScale)
	
	Color(255, 255, 255)
	y = y - ySpace
	If me\Stamina <= 25.0
		RenderBar(t\ImageID[3], x, y, Width, Height, me\Stamina, 100.0, 50, 0, 0)
	Else
		RenderBar(t\ImageID[2], x, y, Width, Height, me\Stamina, 100.0, 50, 50, 50)
	EndIf
	If (PlayerRoom\RoomTemplate\RoomID = r_dimension_106 And (PlayerPosY < 2000.0 * RoomScale Lor PlayerPosY > 2608.0 * RoomScale)) Lor I_714\Using > 0 Lor me\Injuries >= 1.5 Lor me\StaminaEffect > 1.0 Lor wi\HazmatSuit = 1 Lor wi\BallisticVest = 2 Lor I_409\Timer >= 55.0 Lor I_1025\State[0] > 0.0
		Color(200, 0, 0)
		Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
	ElseIf chs\InfiniteStamina Lor me\StaminaEffect < 1.0 Lor wi\GasMask >= 3 Lor I_1499\Using = 2 Lor wi\HazmatSuit >= 3
		Color(0, 200, 0)
		Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
	EndIf
	Color(255, 255, 255)
	Rect(x - IconRectSpace, y, IconRectSize, IconRectSize, False)
	If me\Crouch
		WalkIconID = 2
	ElseIf (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) And me\CurrSpeed > 0.0 And (Not chs\NoClip) And me\Stamina > 0.0
		WalkIconID = 1
	Else
		WalkIconID = 0
	EndIf
	DrawBlock(t\IconID[WalkIconID], x - IconSpace, y + 1)
	
	Color(255, 255, 255)
	y = y - ySpace
	If me\BlinkTimer < 150.0
		RenderBar(t\ImageID[1], x, y, Width, Height, me\BlinkTimer, me\BLINKFREQ, 100, 0, 0)
	Else
		RenderBar(BlinkMeterIMG, x, y, Width, Height, me\BlinkTimer, me\BLINKFREQ)
	EndIf
	If me\BlurTimer > 550.0 Lor me\BlinkEffect > 1.0 Lor me\LightFlash > 0.0 Lor (((me\LightBlink >= 0.25 And (Not chs\NoBlink)) Lor me\EyeIrritation > 0.0) And wi\NightVision = 0)
		Color(200, 0, 0)
		Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
	ElseIf me\BlinkEffect < 1.0 Lor chs\NoBlink
		Color(0, 200, 0)
		Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
	EndIf
	Color(255, 255, 255)
	Rect(x - IconRectSpace, y, IconRectSize, IconRectSize, False)
	If me\BlinkTimer < 0.0
		BlinkIconID = 4
	Else
		BlinkIconID = 3
	EndIf
	DrawBlock(t\IconID[BlinkIconID], x - IconSpace, y + 1)
	
	If (I_714\Using > 0 And Remove714Timer < 500.0) Lor (wi\HazmatSuit > 0 And RemoveHazmatTimer < 500.0)
		Color(255, 255, 255)
		y = y - ySpace
		If wi\HazmatSuit > 0
			If RemoveHazmatTimer < 125.0
				RenderBar(t\ImageID[1], x, y, Width, Height, RemoveHazmatTimer, 500.0, 100, 0, 0)
			Else
				RenderBar(BlinkMeterIMG, x, y, Width, Height, RemoveHazmatTimer, 500.0)
			EndIf
		Else
			If Remove714Timer < 125.0
				RenderBar(t\ImageID[1], x, y, Width, Height, Remove714Timer, 500.0, 100, 0, 0)
			Else
				RenderBar(BlinkMeterIMG, x, y, Width, Height, Remove714Timer, 500.0)
			EndIf
		EndIf
		If wi\HazmatSuit = 4
			Color(0, 200, 0)
			Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
		ElseIf I_714\Using = 1
			Color(200, 0, 0)
			Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
		EndIf
		Color(255, 255, 255)
		Rect(x - IconRectSpace, y, IconRectSize, IconRectSize, False)
		DrawBlock(t\IconID[7], x - IconSpace, y + 1)
	EndIf
	If I_268\Using > 1
		Color(255, 255, 255)
		y = y - ySpace
		If I_268\Timer < 150.0
			RenderBar(t\ImageID[1], x, y, Width, Height, I_268\Timer, 600.0, 100, 0, 0)
		Else
			RenderBar(BlinkMeterIMG, x, y, Width, Height, I_268\Timer, 600.0)
		EndIf
		If I_268\Timer =< 0.0
			Color(150, 150, 0)
			Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
		ElseIf I_714\Using > 0
			Color(200, 0, 0)
			Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
		ElseIf I_268\Using = 3
			Color(0, 200, 0)
			Rect(x - IconColoredRectSpaceX, y - IconColoredRectSpaceY, IconColoredRectSize, IconColoredRectSize)
		EndIf
		Color(255, 255, 255)
		Rect(x - IconRectSpace, y, IconRectSize, IconRectSize, False)
		DrawBlock(t\IconID[8], x - IconSpace, y + 1)
	EndIf
End Function

Function RenderDebugHUD%()
	Local ev.Events, ch.Chunk
	Local x%, y%, i%
	
	x = 20 * MenuScale
	y = 40 * MenuScale
	
	Color(255, 255, 255)
	SetFontEx(fo\FontID[Font_Console])
	
	Select chs\DebugHUD
		Case 1
			;[Block]
			TextEx(x, y, Format(GetLocalString("misc", "room"), PlayerRoom\RoomTemplate\RoomID))
			TextEx(x, y + (20 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_1.xyz"), Floor(EntityX(PlayerRoom\OBJ) / 8.0 + 0.5), "{0}"), Floor(EntityZ(PlayerRoom\OBJ) / 8.0 + 0.5), "{1}"), PlayerRoom\Angle, "{2}"))
			For ev.Events = Each Events
				If ev\room = PlayerRoom
					TextEx(x, y + (40 * MenuScale), Format(GetLocalString("console", "debug_1.event"), ev\EventID))
					TextEx(x, y + (60 * MenuScale), Format(GetLocalString("console", "debug_1.state_1"), ev\EventState))
					TextEx(x, y + (80 * MenuScale), Format(GetLocalString("console", "debug_1.state_2"), ev\EventState2))
					TextEx(x, y + (100 * MenuScale), Format(GetLocalString("console", "debug_1.state_3"), ev\EventState3))
					TextEx(x, y + (120 * MenuScale), Format(GetLocalString("console", "debug_1.state_4"), ev\EventState4))
					TextEx(x, y + (140 * MenuScale), Format(GetLocalString("console", "debug_1.str"), ev\EventStr))
					Exit
				EndIf
			Next
			If PlayerRoom\RoomTemplate\RoomID = r_dimension_1499
				TextEx(x, y + (180 * MenuScale), Format(Format(GetLocalString("console", "debug_1.chunkxyz"), (Int((EntityX(me\Collider) + 20) / 40)), "{0}"), (Int((EntityZ(me\Collider) + 20) / 40)), "{1}"))
				
				Local CH_Amount% = 0
				
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				TextEx(x, y + (200 * MenuScale), Format(GetLocalString("console", "debug_1.currchunk"), CH_Amount))
			Else
				TextEx(x, y + (200 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_1.currroom"), PlayerRoom\x, "{0}"), PlayerRoom\y, "{1}"), PlayerRoom\z, "{2}"))
			EndIf
			
			If sc_I\SelectedMonitor = Null
				TextEx(x, y + (240 * MenuScale), Format(GetLocalString("console", "debug_1.currmon"), "Null"))
			EndIf
			
			If SelectedItem <> Null
				TextEx(x, y + (280 * MenuScale), Format(GetLocalString("console", "debug_1.curritem"), SelectedItem\ItemTemplate\Name))
			ElseIf d_I\ClosestButton = 0
				TextEx(x, y + (280 * MenuScale), Format(GetLocalString("console", "debug_1.currbtn"), "Null"))
			EndIf
			
			TextEx(x, y + (320 * MenuScale), Format(GetLocalString("console", "debug_1.currflo"), PlayerElevatorFloor))
			TextEx(x, y + (340 * MenuScale), Format(GetLocalString("console", "debug_1.roomflo"), ToElevatorFloor))
			If PlayerInsideElevator
				TextEx(x, y + (360 * MenuScale), Format(GetLocalString("console", "debug_1.inelev"), "True"))
			Else
				TextEx(x, y + (360 * MenuScale), Format(GetLocalString("console", "debug_1.inelev"), "False"))
			EndIf
			
			TextEx(x, y + (400 * MenuScale), Format(Format(GetLocalString("console", "debug_1.time"), CurrentDate(), "{0}"), CurrentTime(), "{1}"))
			TextEx(x, y + (420 * MenuScale), Format(Format(GetLocalString("console", "debug_1.vidmem"), ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)), "{0}"), (TotalVidMem() / 1024), "{1}"))
			TextEx(x, y + (440 * MenuScale), Format(Format(GetLocalString("console", "debug_1.glomem"), ((TotalPhys() / 1024) - (AvailPhys() / 1024)), "{0}"), (TotalPhys() / 1024), "{1}"))
			TextEx(x, y + (460 * MenuScale), Format(GetLocalString("console", "debug_1.triamo"), CurrTrisAmount))
			TextEx(x, y + (480 * MenuScale), Format(GetLocalString("console", "debug_1.acttex"), ActiveTextures()))
			;[End Block]
		Case 2
			;[Block]
			TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, PlayerRoom\OBJ)
			TextEx(x, y, Format(Format(Format(GetLocalString("console", "debug_2.ppos"), FloatToString(TFormedX(), 1), "{0}"), FloatToString(TFormedY(), 1), "{1}"), FloatToString(TFormedZ(), 1), "{2}"))
			TextEx(x, y + (20 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_2.pcampos"), FloatToString(EntityX(Camera), 1), "{0}"), FloatToString(EntityY(Camera), 1), "{1}"), FloatToString(EntityZ(Camera), 1), "{2}"))
			TextEx(x, y + (40 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_2.prot"), FloatToString(EntityPitch(me\Collider), 1), "{0}"), FloatToString(EntityYaw(me\Collider), 1), "{1}"), FloatToString(EntityRoll(me\Collider), 1), "{2}"))
			
			TextEx(x, y + (80 * MenuScale), Format(GetLocalString("console", "debug_2.injuries"), me\Injuries))
			TextEx(x, y + (100 * MenuScale), Format(GetLocalString("console", "debug_2.bloodloss"), me\Bloodloss))
			
			TextEx(x, y + (140 * MenuScale), Format(GetLocalString("console", "debug_2.blur"), me\BlurTimer))
			TextEx(x, y + (160 * MenuScale), Format(GetLocalString("console", "debug_2.blink"), me\LightBlink))
			TextEx(x, y + (180 * MenuScale), Format(GetLocalString("console", "debug_2.flash"), me\LightFlash))
			
			TextEx(x, y + (220 * MenuScale), Format(GetLocalString("console", "debug_2.freq"), me\BLINKFREQ))
			TextEx(x, y + (240 * MenuScale), Format(GetLocalString("console", "debug_2.timer"), me\BlinkTimer))
			TextEx(x, y + (260 * MenuScale), Format(GetLocalString("console", "debug_2.effect"), me\BlinkEffect))
			TextEx(x, y + (280 * MenuScale), Format(GetLocalString("console", "debug_2.efftim"), me\BlinkEffectTimer))
			TextEx(x, y + (300 * MenuScale), Format(GetLocalString("console", "debug_2.eyeirr"), me\EyeIrritation))
			TextEx(x, y + (320 * MenuScale), Format(GetLocalString("console", "debug_2.eyestuck"), me\EyeStuck))
			
			TextEx(x, y + (360 * MenuScale), Format(GetLocalString("console", "debug_2.stamina"), me\Stamina))
			TextEx(x, y + (380 * MenuScale), Format(GetLocalString("console", "debug_2.stameff"), me\StaminaEffect))
			TextEx(x, y + (400 * MenuScale), Format(GetLocalString("console", "debug_2.stamtimer"), me\StaminaEffectTimer))
			
			TextEx(x, y + (440 * MenuScale), Format(GetLocalString("console", "debug_2.deaf"), me\DeafTimer))
			TextEx(x, y + (460 * MenuScale), Format(GetLocalString("console", "debug_2.sanity"), me\Sanity))
			
			x = x + (700 * MenuScale)
			
			If me\Terminated
				TextEx(x, y, Format(GetLocalString("console", "debug_2.terminated"), "True"))
			Else
				TextEx(x, y, Format(GetLocalString("console", "debug_2.terminated"), "False"))
			EndIf
			
			TextEx(x, y + (20 * MenuScale), Format(GetLocalString("console", "debug_2.death"), me\DeathTimer))
			TextEx(x, y + (40 * MenuScale), Format(GetLocalString("console", "debug_2.fall"), me\FallTimer))
			
			TextEx(x, y + (80 * MenuScale), Format(GetLocalString("console", "debug_2.heal"), me\HealTimer))
			
			TextEx(x, y + (120 * MenuScale), Format(GetLocalString("console", "debug_2.heartbeat"), me\HeartBeatTimer))
			
			TextEx(x, y + (160 * MenuScale), Format(GetLocalString("console", "debug_2.explosion"), me\ExplosionTimer))
			
			TextEx(x, y + (200 * MenuScale), Format(GetLocalString("console", "debug_2.speed"), me\CurrSpeed))
			
			TextEx(x, y + (240 * MenuScale), Format(GetLocalString("console", "debug_2.camshake"), me\CameraShakeTimer))
			TextEx(x, y + (260 * MenuScale), Format(GetLocalString("console", "debug_2.camzoom"), me\CurrCameraZoom))
			
			TextEx(x, y + (300 * MenuScale), Format(GetLocalString("console", "debug_2.vomit"), me\VomitTimer))
			
			If me\Playable
				TextEx(x, y + (340 * MenuScale), Format(GetLocalString("console", "debug_2.playable"), "True"))
			Else
				TextEx(x, y + (340 * MenuScale), Format(GetLocalString("console", "debug_2.playable"), "False"))
			EndIf
			
			TextEx(x, y + (380 * MenuScale), Format(GetLocalString("console", "debug_2.refitems"), me\RefinedItems))
			TextEx(x, y + (400 * MenuScale), Format(GetLocalString("console", "debug_2.funds"), me\Funds))
			TextEx(x, y + (420 * MenuScale), Format(GetLocalString("console", "debug_2.escape"), EscapeTimer))
			;[End Block]
		Case 3
			;[Block]
			If n_I\Curr049 <> Null
				TextEx(x, y, Format(Format(Format(GetLocalString("console", "debug_3.049pos"), FloatToString(EntityX(n_I\Curr049\OBJ), 2), "{0}"), FloatToString(EntityY(n_I\Curr049\OBJ), 2), "{1}"), FloatToString(EntityZ(n_I\Curr049\OBJ), 2), "{2}"))
				TextEx(x, y + (20 * MenuScale), Format(GetLocalString("console", "debug_3.049idle"), n_I\Curr049\Idle))
				TextEx(x, y + (40 * MenuScale), Format(GetLocalString("console", "debug_3.049state"), n_I\Curr049\State))
			EndIf
			If n_I\Curr096 <> Null
				TextEx(x, y + (60 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_3.096pos"), FloatToString(EntityX(n_I\Curr096\OBJ), 2), "{0}"), FloatToString(EntityY(n_I\Curr096\OBJ), 2), "{1}"), FloatToString(EntityZ(n_I\Curr096\OBJ), 2), "{2}"))
				TextEx(x, y + (80 * MenuScale), Format(GetLocalString("console", "debug_3.096idle"), n_I\Curr096\Idle))
				TextEx(x, y + (100 * MenuScale), Format(GetLocalString("console", "debug_3.096state"), n_I\Curr096\State))
			EndIf
			TextEx(x, y + (120 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_3.106pos"), FloatToString(EntityX(n_I\Curr106\OBJ), 2), "{0}"), FloatToString(EntityY(n_I\Curr106\OBJ), 2), "{1}"), FloatToString(EntityZ(n_I\Curr106\OBJ), 2), "{2}"))
			TextEx(x, y + (140 * MenuScale), Format(GetLocalString("console", "debug_3.106idle"), n_I\Curr106\Idle))
			TextEx(x, y + (160 * MenuScale), Format(GetLocalString("console", "debug_3.106state"), n_I\Curr106\State))
			
			TextEx(x, y + (180 * MenuScale), Format(Format(Format(GetLocalString("console", "debug_3.173pos"), FloatToString(EntityX(n_I\Curr173\OBJ), 2), "{0}"), FloatToString(EntityY(n_I\Curr173\OBJ), 2), "{1}"), FloatToString(EntityZ(n_I\Curr173\OBJ), 2), "{2}"))
			TextEx(x, y + (200 * MenuScale), Format(GetLocalString("console", "debug_3.173idle"), n_I\Curr173\Idle))
			TextEx(x, y + (220 * MenuScale), Format(GetLocalString("console", "debug_3.173state"), n_I\Curr173\State))
			
			TextEx(x, y + (260 * MenuScale), Format(GetLocalString("console", "debug_3.pill"), I_500\Taken))
			
			TextEx(x, y + (300 * MenuScale), Format(GetLocalString("console", "debug_3.008"), I_008\Timer))
			TextEx(x, y + (320 * MenuScale), Format(GetLocalString("console", "debug_3.409"), I_409\Timer))
			TextEx(x, y + (340 * MenuScale), Format(GetLocalString("console", "debug_3.427"), Int(I_427\Timer / 70.0)))
			For i = 0 To 7
				TextEx(x, y + ((360 + (20 * i)) * MenuScale), Format(Format(GetLocalString("console", "debug_3.1025"), i, "{0}"), I_1025\State[i], "{1}"))
			Next
			
			If I_005\ChanceToSpawn < 3
				TextEx(x, y + (540 * MenuScale), GetLocalString("console", "debug_3.005.chamber"))
			ElseIf I_005\ChanceToSpawn < 5
				TextEx(x, y + (540 * MenuScale), GetLocalString("console", "debug_3.005.409"))
			ElseIf I_005\ChanceToSpawn =< 10
				TextEx(x, y + (540 * MenuScale), GetLocalString("console", "debug_3.005.maynard"))
			EndIf
			;[End Block]
	End Select
	SetFontEx(fo\FontID[Font_Default])
End Function

Function RenderGUI%()
	CatchErrors("RenderGUI()")
	
	Local e.Events, it.Items, a_it.Items
	Local Temp%, x%, y%, z%, i%, YawValue#, PitchValue#
	Local x1#, x2#, x3#, y1#, y2#, y3#, z2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$
	Local Width%, Height%
	Local SqrValue#
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	
	If MenuOpen Lor InvOpen Lor ConsoleOpen Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor me\EndingTimer < 0.0
		ShowPointer()
	Else
		HidePointer()
	EndIf
	
	If RID = r_dimension_106
		For e.Events = Each Events
			If e\room = PlayerRoom
				If (wi\NightVision > 0 Lor wi\SCRAMBLE > 0) And e\EventState2 <> PD_FakeTunnelRoom
					If e\Img = 0
						e\Img = LoadImage_Strict("GFX\Overlays\scp_106_face_overlay.png")
						e\Img = ScaleImage2(e\Img, MenuScale, MenuScale)
					Else
						DrawBlock(e\Img, mo\Viewport_Center_X - (Rand(310, 390) * MenuScale), mo\Viewport_Center_Y - (Rand(290, 310) * MenuScale))
					EndIf
				Else
					If e\EventState2 = PD_ThroneRoom
						If me\BlinkTimer > -16.0 And me\BlinkTimer < -6.0
							If e\Img2 = 0
								e\Img2 = LoadImage_Strict("GFX\Overlays\kneel_mortal_overlay.png")
								e\Img2 = ScaleImage2(e\Img2, MenuScale, MenuScale)
							Else
								DrawBlock(e\Img2, mo\Viewport_Center_X - (Rand(310, 390) * MenuScale), mo\Viewport_Center_Y - (Rand(290, 310) * MenuScale))
							EndIf
						EndIf
					EndIf
				EndIf
				Exit
			EndIf
		Next
	EndIf
	
	If I_294\Using Then Render294()
	If SelectedDifficulty\Name <> "Apollyon" And opt\HUDEnabled
		If (Not (MenuOpen Lor InvOpen Lor ConsoleOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated))
			Local CoordEx% = 32 * MenuScale
			
			If d_I\ClosestButton <> 0
				Temp = CreatePivot()
				PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
				PointEntity(Temp, d_I\ClosestButton)
				YawValue = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
				If YawValue > 90.0 And YawValue <= 180.0 Then YawValue = 90.0
				If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
				PitchValue = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
				If PitchValue > 90.0 And PitchValue <= 180.0 Then PitchValue = 90.0
				If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
				
				FreeEntity(Temp) : Temp = 0
				
				DrawBlock(t\IconID[5], mo\Viewport_Center_X + Sin(YawValue) * (opt\GraphicWidth / 3) - CoordEx, mo\Viewport_Center_Y - Sin(PitchValue) * (opt\GraphicHeight / 3) - CoordEx)
			EndIf
			
			If ClosestItem <> Null
				YawValue = -DeltaYaw(Camera, ClosestItem\Collider)
				If YawValue > 90.0 And YawValue <= 180.0 Then YawValue = 90.0
				If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
				PitchValue = -DeltaPitch(Camera, ClosestItem\Collider)
				If PitchValue > 90.0 And PitchValue <= 180.0 Then PitchValue = 90.0
				If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
				
				DrawBlock(t\IconID[6], mo\Viewport_Center_X + Sin(YawValue) * (opt\GraphicWidth / 3) - CoordEx, mo\Viewport_Center_Y - Sin(PitchValue) * (opt\GraphicHeight / 3) - CoordEx)
			EndIf
			
			If DrawHandIcon Then DrawBlock(t\IconID[5], mo\Viewport_Center_X - CoordEx, mo\Viewport_Center_Y - CoordEx)
			
			For i = 0 To 3
				x = mo\Viewport_Center_X - CoordEx
				y = mo\Viewport_Center_Y - CoordEx
				If DrawArrowIcon[i]
					Select i
						Case 0
							;[Block]
							y = y - (69 * MenuScale)
							;[End Block]
						Case 1
							;[Block]
							x = x + (69 * MenuScale)
							;[End Block]
						Case 2
							;[Block]
							y = y + (69 * MenuScale)
							;[End Block]
						Case 3
							;[Block]
							x = x - (69 * MenuScale)
							;[End Block]
					End Select
					DrawBlock(t\IconID[i + 10], x, y)
				EndIf
			Next
		EndIf
		
		RenderHUD()
	EndIf
	If chs\DebugHUD <> 0 Then RenderDebugHUD()
	
	If SelectedScreen <> Null Then DrawBlock(SelectedScreen\Img, mo\Viewport_Center_X - ImageWidth(SelectedScreen\Img) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedScreen\Img) / 2)
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If d_I\SelectedDoor <> Null
		If SelectedItem <> Null
			If SelectedItem\ItemTemplate\TempName = "scp005" Then ShouldDrawHUD = False
		EndIf
		If ShouldDrawHUD
			Local ButtonPosX# = EntityX(d_I\ClosestButton, True)
			Local ButtonPosY# = EntityY(d_I\ClosestButton, True)
			Local ButtonPosZ# = EntityZ(d_I\ClosestButton, True)
			
			CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * opt\RealGraphicWidth / opt\RealGraphicHeight)) / 2.0))
			Pvt = CreatePivot()
			PositionEntity(Pvt, ButtonPosX, ButtonPosY, ButtonPosZ)
			RotateEntity(Pvt, 0.0, EntityYaw(d_I\ClosestButton, True) - 180.0, 0.0)
			MoveEntity(Pvt, 0.0, 0.0, 0.22)
			PositionEntity(Camera, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
			PointEntity(Camera, d_I\ClosestButton)
			FreeEntity(Pvt) : Pvt = 0
			
			CameraProject(Camera, ButtonPosX, ButtonPosY + (MeshHeight(d_I\ButtonModelID[BUTTON_DEFAULT_MODEL]) * 0.015), ButtonPosZ)
			ProjY = ProjectedY()
			CameraProject(Camera, ButtonPosX, ButtonPosY - (MeshHeight(d_I\ButtonModelID[BUTTON_DEFAULT_MODEL]) * 0.015), ButtonPosZ)
			Scale = (ProjectedY() - ProjY) / (462.0 * MenuScale)
			
			x = mo\Viewport_Center_X - ImageWidth(t\ImageID[4]) * (Scale / 2)
			y = mo\Viewport_Center_Y - ImageHeight(t\ImageID[4]) * (Scale / 2)
			
			SetFontEx(fo\FontID[Font_Digital])
			Color(255, 255, 255)
			If msg\KeyPadMsg <> ""
				If (msg\KeyPadTimer Mod 70.0) < 35.0 Then TextEx(mo\Viewport_Center_X, y + (124 * MenuScale * Scale), msg\KeyPadMsg, True, True)
			Else
				TextEx(mo\Viewport_Center_X, y + (70 * MenuScale * Scale), GetLocalString("msg", "accesscode"), True, True)
				SetFontEx(fo\FontID[Font_Digital_Big])
				TextEx(mo\Viewport_Center_X, y + (124 * MenuScale * Scale), msg\KeyPadInput, True, True)
			EndIf
			RenderCursor()
		EndIf
	EndIf
	
	Local PrevOtherOpen.Items
	Local OtherSize%, OtherAmount%
	Local IsEmpty%
	Local IsMouseOn%
	Local ClosedInv%
	Local INVENTORY_GFX_SIZE% = 70 * MenuScale
	Local INVENTORY_GFX_SPACING% = 35 * MenuScale
	Local InvImgSize% = (64 * MenuScale) / 2
	Local MaxItemAmountHalf% = MaxItemAmount / 2
	
	If OtherOpen <> Null
		PrevOtherOpen = OtherOpen
		OtherSize = OtherOpen\InvSlots
		
		For i = 0 To OtherSize - 1
			If OtherOpen\SecondInv[i] <> Null Then OtherAmount = OtherAmount + 1
		Next
		
		Local TempX% = 0
		
		x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * 10 / 2) + (INVENTORY_GFX_SPACING * ((10 / 2) - 1))) / 2
		y = mo\Viewport_Center_Y - (INVENTORY_GFX_SIZE * ((OtherSize / 10 * 2) - 1)) - INVENTORY_GFX_SPACING
		
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + (2 * MenuScale), INVENTORY_GFX_SIZE + (2 * MenuScale))
			EndIf
			
			RenderFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null
				If (IsMouseOn = n Lor SelectedItem <> OtherOpen\SecondInv[n]) Then DrawBlock(OtherOpen\SecondInv[n]\InvImg, x + (INVENTORY_GFX_SIZE / 2) - (32 * MenuScale), y + (INVENTORY_GFX_SIZE / 2) - (32 * MenuScale))
			EndIf
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n]
				If IsMouseOn = n
					Color(255, 255, 255)
					TextEx(x + (INVENTORY_GFX_SIZE / 2), y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - (15 * MenuScale), OtherOpen\SecondInv[n]\ItemTemplate\DisplayName, True)
				EndIf
			EndIf
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			TempX = TempX + 1
			If TempX = 5
				TempX = 0
				y = y + (INVENTORY_GFX_SIZE * 2)
				x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * 10 / 2) + (INVENTORY_GFX_SPACING * ((10 / 2) - 1))) / 2
			EndIf
		Next
		
		If SelectedItem <> Null
			If mo\MouseDown1
				If MouseSlot = 66
					DrawBlock(SelectedItem\InvImg, MousePosX - InvImgSize, MousePosY - InvImgSize)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawBlock(SelectedItem\InvImg, MousePosX - InvImgSize, MousePosY - InvImgSize)
				EndIf
			EndIf
		EndIf
		
		RenderCursor()
	ElseIf InvOpen
		x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + (INVENTORY_GFX_SPACING * (MaxItemAmountHalf - 1))) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		If MaxItemAmount = 2
			y = y + INVENTORY_GFX_SIZE
			x = x - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + INVENTORY_GFX_SPACING) / 2
		EndIf
		
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If Inventory(n) <> Null
				Local ShouldDrawRect%
				
				Color(200, 200, 200)
				Select Inventory(n)\ItemTemplate\TempName 
					Case "gasmask"
						;[Block]
						ShouldDrawRect = (wi\GasMask = 1)
						;[End Block]
					Case "finegasmask"
						;[Block]
						ShouldDrawRect = (wi\GasMask = 2)
						;[End Block]
					Case "veryfinegasmask"
						;[Block]
						ShouldDrawRect = (wi\GasMask = 3)
						;[End Block]
					Case "gasmask148"
						;[Block]
						ShouldDrawRect = (wi\GasMask = 4)
						;[End Block]
					Case "scp1499"
						;[Block]
						ShouldDrawRect = (I_1499\Using = 1)
						;[End Block]
					Case "fine1499"
						;[Block]
						ShouldDrawRect = (I_1499\Using = 2)
						;[End Block]
					Case "nvg"
						;[Block]
						ShouldDrawRect = (wi\NightVision = 1)
						;[End Block]
					Case "veryfinenvg"
						;[Block]
						ShouldDrawRect = (wi\NightVision = 2)
						;[End Block]
					Case "finenvg"
						;[Block]
						ShouldDrawRect = (wi\NightVision = 3)
						;[End Block]
					Case "scramble"
						;[Block]
						ShouldDrawRect = (wi\SCRAMBLE = 1)
						;[End Block]
					Case "finescramble"
						;[Block]
						ShouldDrawRect = (wi\SCRAMBLE = 2)
						;[End Block]
					Case "helmet"
						;[Block]
						ShouldDrawRect = wi\BallisticHelmet
						;[End Block]
					Case "cap"
						;[Block]
						ShouldDrawRect = (I_268\Using = 1)
						;[End Block]
					Case "scp268"
						;[Block]
						ShouldDrawRect = (I_268\Using = 2)
						;[End Block]
					Case "fine268"
						;[Block]
						ShouldDrawRect = (I_268\Using = 3)
						;[End Block]
					Case "vest"
						;[Block]
						ShouldDrawRect = (wi\BallisticVest = 1)
						;[End Block]
					Case "finevest"
						;[Block]
						ShouldDrawRect = (wi\BallisticVest = 2)
						;[End Block]
					Case "hazmatsuit"
						;[Block]
						ShouldDrawRect = (wi\HazmatSuit = 1)
						;[End Block]
					Case "finehazmatsuit"
						;[Block]
						ShouldDrawRect = (wi\HazmatSuit = 2)
						;[End Block]
					Case "veryfinehazmatsuit"
						;[Block]
						ShouldDrawRect = (wi\HazmatSuit = 3)
						;[End Block]
					Case "hazmatsuit148"
						;[Block]"
						ShouldDrawRect = (wi\HazmatSuit = 4)
						;[End Block]
					Case "scp427"
						;[Block]
						ShouldDrawRect = (I_427\Using)
						;[End Block]
					Case "scp714"
						;[Block]
						ShouldDrawRect = (I_714\Using = 2)
						;[End Block]
					Case "coarse714"
						;[Block]
						ShouldDrawRect = (I_714\Using = 1)
						;[End Block]
					Default
						;[Block]
						ShouldDrawRect = False
						;[End Block]
				End Select
				If ShouldDrawRect Then Rect(x - (3 * MenuScale), y - (3 * MenuScale), INVENTORY_GFX_SIZE + (6 * MenuScale), INVENTORY_GFX_SIZE + (6 * MenuScale))
			EndIf
			
			If IsMouseOn = n
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + (2 * MenuScale), INVENTORY_GFX_SIZE + (2 * MenuScale))
			EndIf
			
			Color(255, 255, 255)
			RenderFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null
				If IsMouseOn = n Lor SelectedItem <> Inventory(n) Then DrawBlock(Inventory(n)\InvImg, x + (INVENTORY_GFX_SIZE / 2) - (32 * MenuScale), y + (INVENTORY_GFX_SIZE / 2) - (32 * MenuScale))
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n)
				If IsMouseOn = n
					If SelectedItem = Null
						SetFontEx(fo\FontID[Font_Default])
						Color(255, 255, 255)
						TextEx(x + (INVENTORY_GFX_SIZE / 2), y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - (15 * MenuScale), Inventory(n)\DisplayName, True)
					EndIf
				EndIf
			EndIf
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If MaxItemAmount >= 4 And n = MaxItemAmountHalf - 1
				y = y + (INVENTORY_GFX_SIZE * 2)
				x = mo\Viewport_Center_X - ((INVENTORY_GFX_SIZE * MaxItemAmountHalf) + (INVENTORY_GFX_SPACING * (MaxItemAmountHalf - 1))) / 2
			EndIf
		Next
		
		If SelectedItem <> Null
			If mo\MouseDown1
				If MouseSlot = 66
					DrawBlock(SelectedItem\InvImg, MousePosX - InvImgSize, MousePosY - InvImgSize)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawBlock(SelectedItem\InvImg, MousePosX - InvImgSize, MousePosY - InvImgSize)
				EndIf
			EndIf
		EndIf
		
		RenderCursor()
	Else
		If SelectedItem <> Null
			Select SelectedItem\ItemTemplate\TempName
				Case "gasmask", "finegasmask", "veryfinegasmask", "gasmask148"
					;[Block]
					If (Not PreventItemOverlapping(True))
						Select SelectedItem\ItemTemplate\TempName
							Case "gasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 1) Then Return
								;[End Block]
							Case "finegasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 2) Then Return
								;[End Block]
							Case "veryfinegasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 3) Then Return
								;[End Block]
							Case "gasmask148"
								;[Block]
								If IsDoubleItem(wi\GasMask, 4) Then Return
								;[End Block]
						End Select
						
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "scp1499", "fine1499"
					;[Block]
					If (Not PreventItemOverlapping(False, False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "scp1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 1) Then Return
								;[End Block]
							Case "fine1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 2) Then Return
								;[End Block]
						End Select
						
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "nvg", "veryfinenvg", "finenvg"
					;[Block]
					If (Not PreventItemOverlapping(False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "nvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 1) Then Return
								;[End Block]
							Case "veryfinenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 2) Then Return
								;[End Block]
							Case "finenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 3) Then Return
								;[End Block]
						End Select
						
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State3)
					EndIf
					;[End Block]
				Case "scramble", "finescramble"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, True))
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State3)
					EndIf
					;[End Block]
				Case "helmet"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, True))
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "cap", "scp268", "fine268"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, False, False, True))
						Select SelectedItem\ItemTemplate\TempName
							Case "cap"
								;[Block]
								If IsDoubleItem(I_268\Using, 1) Then Return
								;[End Block]
							Case "scp268"
								;[Block]
								If IsDoubleItem(I_268\Using, 2) Then Return
								;[End Block]
							Case "fine268"
								;[Block]
								If IsDoubleItem(I_268\Using, 3) Then Return
								;[End Block]
						End Select
						
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
					
					Width = 300 * MenuScale
					Height = 20 * MenuScale
					x = mo\Viewport_Center_X - (Width / 2)
					y = mo\Viewport_Center_Y + (80 * MenuScale)
					
					RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					;[End Block]
				Case "hazmatsuit", "finehazmatsuit", "veryfinehazmatsuit", "hazmatsuit148"
					;[Block]
					DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
					
					Width = 300 * MenuScale
					Height = 20 * MenuScale
					x = mo\Viewport_Center_X - (Width / 2)
					y = mo\Viewport_Center_Y + (80 * MenuScale)
					
					RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					;[End Block]
				Case "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "hand3", "25ct", "scp005", "key", "coin", "mastercard"
					;[Block]
					DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If (me\Bloodloss <> 0.0 Lor me\Injuries <> 0.0) And wi\HazmatSuit = 0
						DrawBlock(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - InvImgSize, mo\Viewport_Center_Y - InvImgSize)
						
						Width = 300 * MenuScale
						Height = 20 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + (80 * MenuScale)
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "paper", "oldpaper"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						Select SelectedItem\ItemTemplate\Name
							Case "Burnt Note" 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(0, 0, 0)
								SetFontEx(fo\FontID[Font_Default])
								TextEx(277 * MenuScale, 469 * MenuScale, CODE_DR_MAYNARD, True, True)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Unknown Note"
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(50, 50, 50)
								SetFontEx(fo\FontID[Font_Journal])
								TextEx(300 * MenuScale, 295 * MenuScale, CODE_O5_COUNCIL, True, True)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Document SCP-372"
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(37, 45, 137)
								SetFontEx(fo\FontID[Font_Journal])
								TextEx(383 * MenuScale, 734 * MenuScale, CODE_MAINTENANCE_TUNNELS, True, True)
								SetBuffer(BackBuffer())
								;[End Block]
							Default 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								;[End Block]
						End Select
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
					EndIf
					DrawBlock(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - SelectedItem\ItemTemplate\ImgWidth, mo\Viewport_Center_Y - SelectedItem\ItemTemplate\ImgHeight)
					;[End Block]
				Case "scp1025"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(ItemHUDTexturePath + "page_1025(" + (Int(SelectedItem\State) + 1) + ").png")
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
					EndIf
					DrawBlock(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - SelectedItem\ItemTemplate\ImgWidth, mo\Viewport_Center_Y - SelectedItem\ItemTemplate\ImgHeight)
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					; ~ RadioState[5] = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState[6] = A timer for the "code channel"
					; ~ RadioState[7] = Another timer for the "code channel"
					
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img)
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img)
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					StrTemp = ""
					
					x = opt\GraphicWidth - SelectedItem\ItemTemplate\ImgWidth
					y = opt\GraphicHeight - SelectedItem\ItemTemplate\ImgHeight
					
					DrawImage(SelectedItem\ItemTemplate\Img, x, y)
					
					If SelectedItem\State > 0.0 Lor (SelectedItem\ItemTemplate\TempName = "fineradio" Lor SelectedItem\ItemTemplate\TempName = "veryfineradio")
						If RID <> r_dimension_106 And CoffinDistance >= 8.0
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									If (Not opt\EnableUserTracks)
										StrTemp = Format(GetLocalString("radio", "usertrack"), GetLocalString("radio", "notenable"))
									ElseIf UserTrackMusicAmount < 1
										StrTemp = Format(GetLocalString("radio", "usertrack"), GetLocalString("radio", "nofound"))
									Else
										If ChannelPlaying(RadioCHN[0]) Then StrTemp = Format(GetLocalString("radio", "usertrack"), Upper(UserTrackName[RadioState[0]]))
									EndIf
									;[End Block]
								Case 1
									;[Block]
									StrTemp = GetLocalString("radio", "warn")
									;[End Block]
								Case 2
									;[Block]
									StrTemp = GetLocalString("radio", "onsite")
									;[End Block]
								Case 3
									;[Block]
									StrTemp = GetLocalString("radio", "emergency")
									;[End Block]
							End Select
							
							x = x + (66 * MenuScale)
							y = y + (419 * MenuScale)
							
							; ~ Battery
							Color(30, 30, 30)
							If SelectedItem\ItemTemplate\TempName = "radio" Lor SelectedItem\ItemTemplate\TempName = "18vradio"
								For i = 0 To 4
									Rect(x, y + ((8 * i) * MenuScale), (43 * MenuScale) - ((i * 6) * MenuScale), 4 * MenuScale, Ceil(SelectedItem\State / 20.0) > 4 - i )
								Next
							EndIf
							
							SetFontEx(fo\FontID[Font_Digital])
							TextEx(x + (60 * MenuScale), y, GetLocalString("radio", "chn"))
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio"
								StrTemp = ""
								For i = 0 To Rand(5, 30)
									StrTemp = StrTemp + Chr(Rand(100))
								Next
								
								SetFontEx(fo\FontID[Font_Digital_Big])
								TextEx(x + (97 * MenuScale), y + (16 * MenuScale), Rand(0, 9), True, True)
							Else
								SetFontEx(fo\FontID[Font_Digital_Big])
								TextEx(x + (97 * MenuScale), y + (16 * MenuScale), Int(SelectedItem\State2 + 1.0), True, True)
							EndIf
							
							SetFontEx(fo\FontID[Font_Digital])
							If StrTemp <> ""
								StrTemp = Right(Left(StrTemp, (Int(MilliSec / 300) Mod Len(StrTemp))), 10)
								TextEx(x - (28 * MenuScale), y + (33 * MenuScale), "          " + StrTemp + "          ")
							EndIf
							SetFontEx(fo\FontID[Font_Default])
						EndIf
					EndIf
					;[End Block]
				Case "nav", "nav300", "nav310", "navulti"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					Local NavType%
					
					Select SelectedItem\ItemTemplate\TempName
						Case "nav300"
							;[Block]
							NavType = 300
							;[End Block]
						Case "nav310"
							;[Block]
							NavType = 310
							;[End Block]
						Case "navulti"
							;[Block]
							NavType = 999
							;[End Block]
						Default
							;[Block]
							NavType = 1
							;[End Block]
					End Select
					
					x = opt\GraphicWidth - SelectedItem\ItemTemplate\ImgWidth + (20 * MenuScale)
					y = opt\GraphicHeight - SelectedItem\ItemTemplate\ImgHeight - (85 * MenuScale)
					
					DrawImage(SelectedItem\ItemTemplate\Img, x - SelectedItem\ItemTemplate\ImgWidth, y - SelectedItem\ItemTemplate\ImgHeight + (85 * MenuScale))
					
					SetFontEx(fo\FontID[Font_Digital])
					
					Local Offline% = (NavType = 300 Lor NavType = 1)
					Local NAV_WIDTH% = 287 * MenuScale
					Local NAV_HEIGHT% = 256 * MenuScale
					Local RectSize% = 24 * MenuScale
					Local RectSizeHalf% = RectSize / 2
					
					If (Not PlayerInReachableRoom())
						If (MilliSec Mod 800) < 200
							Color(200, 0, 0)
							TextEx(x, y + (NAV_HEIGHT / 2) - (80 * MenuScale), GetLocalString("msg", "nav.error"), True)
							TextEx(x, y + (NAV_HEIGHT / 2) - (60 * MenuScale), GetLocalString("msg", "nav.locunknown"), True)
						EndIf
						Return
					Else
						If (SelectedItem\State > 0.0 Lor NavType = 300 Lor NavType = 999) And (Rnd(CoffinDistance + 15.0) > 1.0 Lor RID <> r_cont1_895)
							Local ColliderX# = EntityX(me\Collider)
							Local ColliderZ# = EntityZ(me\Collider)
							Local PlayerX% = Floor(ColliderX / RoomSpacing + 0.5)
							Local PlayerZ% = Floor(ColliderZ / RoomSpacing + 0.5)
							
							SetBuffer(ImageBuffer(t\ImageID[7]))
							
							Local xx% = x - SelectedItem\ItemTemplate\ImgWidth
							Local yy% = y - SelectedItem\ItemTemplate\ImgHeight + (85 * MenuScale)
							
							DrawImage(SelectedItem\ItemTemplate\Img, xx, yy)
							
							x = x - (12 * MenuScale) + ((ColliderX - 4.0) Mod RoomSpacing) * (3 * MenuScale)
							y = y + (12 * MenuScale) - ((ColliderZ - 4.0) Mod RoomSpacing) * (3 * MenuScale)
							For x2 = Max(1.0, PlayerX - 6) To Min(MapGridSize - 1, PlayerX + 6)
								For z2 = Max(1.0, PlayerZ - 6) To Min(MapGridSize - 1, PlayerZ + 6)
									If CoffinDistance > 16.0 Lor Rnd(16.0) < CoffinDistance
										If CurrMapGrid\Grid[x2 + (z2 * MapGridSize)] > MapGrid_NoTile And (CurrMapGrid\Found[x2 + (z2 * MapGridSize)] > MapGrid_NoTile Lor (Not Offline))
											Local DrawX% = x + (PlayerX - x2) * RectSize, DrawY% = y - (PlayerZ - z2) * RectSize
											
											Color(30, 30, 30)
											If CurrMapGrid\Grid[(x2 + 1) + (z2 * MapGridSize)] = MapGrid_NoTile Then Rect(DrawX - RectSizeHalf, DrawY - RectSizeHalf, 1, RectSize)
											If CurrMapGrid\Grid[(x2 - 1) + (z2 * MapGridSize)] = MapGrid_NoTile Then Rect(DrawX + RectSizeHalf, DrawY - RectSizeHalf, 1, RectSize)
											
											If CurrMapGrid\Grid[x2 + ((z2 - 1) * MapGridSize)] = MapGrid_NoTile Then Rect(DrawX - RectSizeHalf, DrawY - RectSizeHalf, RectSize, 1)
											If CurrMapGrid\Grid[x2 + ((z2 + 1) * MapGridSize)] = MapGrid_NoTile Then Rect(DrawX - RectSizeHalf, DrawY + RectSizeHalf, RectSize, 1)
										EndIf
									EndIf
								Next
							Next
							
							SetBuffer(BackBuffer())
							DrawBlockRect(t\ImageID[7], xx + (80 * MenuScale), yy + (70 * MenuScale), xx + (80 * MenuScale), yy + (70 * MenuScale), 270 * MenuScale, 230 * MenuScale)
							If Offline
								Color(100, 0, 0)
							Else
								Color(30, 30, 30)
							EndIf
							Rect(xx + (80 * MenuScale), yy + (70 * MenuScale), 270 * MenuScale, 230 * MenuScale, False)
							
							x = opt\GraphicWidth - SelectedItem\ItemTemplate\ImgWidth + (20 * MenuScale)
							y = opt\GraphicHeight - SelectedItem\ItemTemplate\ImgHeight - (85 * MenuScale)
							
							If Offline
								Color(100, 0, 0)
							Else
								Color(30, 30, 30)
							EndIf
							If (MilliSec Mod 800) < 200 ; ~ TODO: FIND THE WAY TO GET RID OF MILLISECS
								If Offline Then TextEx(x - (NAV_WIDTH / 2) + (10 * MenuScale), y - (NAV_HEIGHT / 2) + (10 * MenuScale), GetLocalString("msg", "nav.data"))
								
								YawValue = EntityYaw(me\Collider) - 90.0
								x1 = x + Cos(YawValue) * (6.0 * MenuScale) : y1 = y - Sin(YawValue) * (6.0 * MenuScale)
								x2 = x + Cos(YawValue - 140.0) * (5.0 * MenuScale) : y2 = y - Sin(YawValue - 140.0) * (5.0 * MenuScale)
								x3 = x + Cos(YawValue + 140.0) * (5.0 * MenuScale) : y3 = y - Sin(YawValue + 140.0) * (5.0 * MenuScale)
								
								Line(x1, y1, x2, y2)
								Line(x1, y1, x3, y3)
								Line(x2, y2, x3, y3)
							EndIf
							
							Local SCPs_Found% = 0, Dist#
							
							If NavType = 999 And (MilliSec Mod 600) < 400
								Local np.NPCs
								
								For np.NPCs = Each NPCs
									If np\NPCType = NPCType173 Lor np\NPCType = NPCType106 Lor np\NPCType = NPCType096 Lor np\NPCType = NPCType049 Lor np\NPCType = NPCType066
										If (Not np\HideFromNVG)
											Dist = EntityDistanceSquared(Camera, np\Collider)
											If Dist < 900.0
												SqrValue = Sqr(Dist)
												Color(100, 0, 0)
												Oval(x - (SqrValue * (1.5 * MenuScale)), y - (SqrValue * (1.5 * MenuScale)), SqrValue * (3 * MenuScale), SqrValue * (3 * MenuScale), False)
												TextEx(x - (NAV_WIDTH / 2) + (10 * MenuScale), y - (NAV_HEIGHT / 2) + (30 * MenuScale) + ((20 * SCPs_Found) * MenuScale), np\NVGName)
												SCPs_Found = SCPs_Found + 1
											EndIf
										EndIf
									EndIf
								Next
								If RID = r_cont1_895
									If CoffinDistance < 8.0
										Dist = Rnd(4.0, 8.0)
										Color(100, 0, 0)
										Oval(x - (Dist * (1.5 * MenuScale)), y - (Dist * (1.5 * MenuScale)), Dist * (3 * MenuScale), Dist * (3 * MenuScale), False)
										TextEx(x - (NAV_WIDTH / 2) + (10 * MenuScale), y - (NAV_HEIGHT / 2) + (30 * MenuScale) + ((20 * SCPs_Found) * MenuScale), "SCP-895")
									EndIf
								EndIf
							EndIf
							
							Color(30, 30, 30)
							If SelectedItem\State > 0.0 And (NavType = 1 Lor NavType = 310)
								xTemp = x - (NAV_WIDTH / 2) + (196 * MenuScale)
								yTemp = y - (NAV_HEIGHT / 2) + (10 * MenuScale)
								
								If Offline Then Color(100, 0, 0)
								Rect(xTemp, yTemp, 80 * MenuScale, 20 * MenuScale, False)
								
								; ~ Battery
								If SelectedItem\State <= 20.0
									Color(100, 0, 0)
								Else
									Color(30, 30, 30)
								EndIf
								For i = 1 To Min(Ceil(SelectedItem\State / 10.0), 10.0)
									Rect(xTemp + ((i * 8) * MenuScale) - (6 * MenuScale), yTemp + (4 * MenuScale), 4 * MenuScale, 12 * MenuScale)
								Next
								SetFontEx(fo\FontID[Font_Digital])
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
					EndIf
					DrawBlock(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - SelectedItem\ItemTemplate\ImgWidth, mo\Viewport_Center_Y - SelectedItem\ItemTemplate\ImgHeight)
					;[End Block]
				Case "oldbadge", "ticket"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						SelectedItem\ItemTemplate\ImgWidth = ImageWidth(SelectedItem\ItemTemplate\Img) / 2
						SelectedItem\ItemTemplate\ImgHeight = ImageHeight(SelectedItem\ItemTemplate\Img) / 2
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					DrawImage(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - SelectedItem\ItemTemplate\ImgWidth, mo\Viewport_Center_Y - SelectedItem\ItemTemplate\ImgHeight)
					;[End Block]
			End Select
		EndIf
	EndIf
	
	CatchErrors("Uncaught: RenderGUI()")
End Function

Type InGameMenu
	Field AchievementsMenu% = 0
	Field QuitMenu% = 0
	Field OptionsMenu% = 0
End Type

Global igm.InGameMenu

; ~ Menu Tab Options Constants
;[Block]
Const MenuTab_Options_Graphics% = 2
Const MenuTab_Options_Audio% = 3
Const MenuTab_Options_Controls% = 4
Const MenuTab_Options_Advanced% = 5
;[End Block]

Global MenuOpen%

Function UpdateMenu%()
	CatchErrors("UpdateMenu()")
	
	Local r.Rooms, sc.SecurityCams
	Local x%, y%, z%, Width%, Height%, i%
	
	If MenuOpen
		If (Not IsPlayerOutsideFacility()) And (Not me\Terminated)
			If me\StopHidingTimer = 0.0
				If (EntityDistanceSquared(n_I\Curr173\Collider, me\Collider) < 0.64 And n_I\Curr173\Idle < 2) Lor EntityDistanceSquared(n_I\Curr106\Collider, me\Collider) < 0.64 Lor (n_I\Curr049 <> Null And EntityDistanceSquared(n_I\Curr049\Collider, me\Collider) < 0.64) And (n_I\Curr066 <> Null And EntityDistanceSquared(n_I\Curr066\Collider, me\Collider) < 0.64) And (n_I\Curr096 <> Null And EntityDistanceSquared(n_I\Curr096\Collider, me\Collider) < 0.64) Then me\StopHidingTimer = 1.0
			ElseIf me\StopHidingTimer < Rnd(120.0, 240.0)
				me\StopHidingTimer = me\StopHidingTimer + 1
			Else
				me\StopHidingTimer = 0.0
				PlaySound_Strict(HorrorSFX[15])
				CreateHintMsg(GetLocalString("msg", "stophiding"))
				ShouldDeleteGadgets = True
				MenuOpen = False
				Return
			EndIf
		EndIf
		
		InvOpen = False
		
		Width = ImageWidth(t\ImageID[0])
		Height = ImageHeight(t\ImageID[0])
		x = mo\Viewport_Center_X - (Width / 2)
		y = mo\Viewport_Center_Y - (Height / 2)
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		If (Not mo\MouseDown1) Then OnSliderID = 0
		
		If igm\AchievementsMenu <= 0 And igm\OptionsMenu > 0 And igm\QuitMenu <= 0
			If igm\OptionsMenu = 1
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "grap"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Graphics, False)
				If UpdateMenuButton(x, y + (75 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "audio"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Audio, False)
				If UpdateMenuButton(x, y + (150 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "ctrl"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Controls, False)
				If UpdateMenuButton(x, y + (225 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "avc"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Advanced, False)
				
				If UpdateMenuButton(x + (101 * MenuScale), y + (455 * MenuScale), 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
					igm\AchievementsMenu = 0
					igm\OptionsMenu = 0
					igm\QuitMenu = 0
					ResetInput()
					
					ShouldDeleteGadgets = True
				EndIf
			Else
				If UpdateMenuButton(x + (101 * MenuScale), y + (455 * MenuScale), 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
					igm\AchievementsMenu = 0
					igm\OptionsMenu = 1
					igm\QuitMenu = 0
					ResetInput()
					SaveOptionsINI()
					
					AntiAlias(opt\AntiAliasing)
					TextureLodBias(opt\TextureDetailsLevel)
					TextureAnisotropic(opt\AnisotropicLevel)
					ShouldDeleteGadgets = True
				EndIf
				
				x = x + (270 * MenuScale)
				
				Select igm\OptionsMenu
					Case MenuTab_Options_Graphics
						;[Block]
						opt\BumpEnabled = UpdateMenuTick(x, y, opt\BumpEnabled, True)
						
						y = y + (30 * MenuScale)
						
						opt\VSync = UpdateMenuTick(x, y, opt\VSync)
						
						y = y + (30 * MenuScale)
						
						opt\AntiAliasing = UpdateMenuTick(x, y, opt\AntiAliasing, opt\DisplayMode <> 0)
						
						y = y + (30 * MenuScale)
						
						opt\AdvancedRoomLights = UpdateMenuTick(x, y, opt\AdvancedRoomLights)
						
						y = y + (40 * MenuScale)
						
						opt\ScreenGamma = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\ScreenGamma * 50.0, 1) / 50.0
						
						y = y + (45 * MenuScale)
						
						opt\ParticleAmount = UpdateMenuSlider3(x, y, 100 * MenuScale, opt\ParticleAmount, 2, GetLocalString("options", "min"), GetLocalString("options", "red"), GetLocalString("options", "full"))
						
						y = y + (45 * MenuScale)
						
						opt\TextureDetails = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
						Select opt\TextureDetails
							Case 0
								;[Block]
								opt\TextureDetailsLevel = 0.8
								;[End Block]
							Case 1
								;[Block]
								opt\TextureDetailsLevel = 0.4
								;[End Block]
							Case 2
								;[Block]
								opt\TextureDetailsLevel = 0.0
								;[End Block]
							Case 3
								;[Block]
								opt\TextureDetailsLevel = -0.4
								;[End Block]
							Case 4
								;[Block]
								opt\TextureDetailsLevel = -0.8
								;[End Block]
						End Select
						TextureLodBias(opt\TextureDetailsLevel)
						
						y = y + (35 * MenuScale)
						
						opt\SaveTexturesInVRAM = UpdateMenuTick(x, y, opt\SaveTexturesInVRAM, True)
						
						y = y + (40 * MenuScale)
						
						opt\CurrFOV = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\CurrFOV * 2.0, 4) / 2.0
						opt\FOV = opt\CurrFOV + 40
						CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * opt\RealGraphicWidth / opt\RealGraphicHeight)) / 2.0))
						
						y = y + (45 * MenuScale)
						
						opt\Anisotropic = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\Anisotropic, 5, "Trilinear", "2x", "4x", "8x", "16x")
						Select opt\Anisotropic
							Case 0
								;[Block]
								opt\AnisotropicLevel = 0
								;[End Block]
							Case 1
								;[Block]
								opt\AnisotropicLevel = 2
								;[End Block]
							Case 2
								;[Block]
								opt\AnisotropicLevel = 4
								;[End Block]
							Case 3
								;[Block]
								opt\AnisotropicLevel = 8
								;[End Block]
							Case 4
								;[Block]
								opt\AnisotropicLevel = 16
								;[End Block]
						End Select
						TextureAnisotropic(opt\AnisotropicLevel)
						
						y = y + (35 * MenuScale)
						
						opt\Atmosphere = UpdateMenuTick(x, y, opt\Atmosphere, True)
						
						y = y + (45 * MenuScale)
						
						opt\SecurityCamRenderInterval = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\SecurityCamRenderInterval, 17, "24.0", "18.0", "12.0", "6.0", "0.0")
						Select opt\SecurityCamRenderInterval
							Case 0
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 24.0
								;[End Block]
							Case 1
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 18.0
								;[End Block]
							Case 2
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 12.0
								;[End Block]
							Case 3
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 6.0
								;[End Block]
							Case 4
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 0.0
								;[End Block]
						End Select
						For sc.SecurityCams = Each SecurityCams
							If sc\Screen Then sc\RenderInterval = opt\SecurityCamRenderIntervalLevel
						Next
						;[End Block]
					Case MenuTab_Options_Audio
						;[Block]
						opt\PrevMasterVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\MasterVolume * 100.0, 1) / 100.0
						If (Not me\Deaf) Then opt\MasterVolume = opt\PrevMasterVolume
						
						y = y + (40 * MenuScale)
						
						opt\MusicVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\MusicVolume * 100.0, 2) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\SFXVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\SFXVolume * 100.0, 3) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\VoiceVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\VoiceVolume * 100.0, 18) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\EnableSFXRelease = UpdateMenuTick(x, y, opt\EnableSFXRelease, True)
						
						y = y + (30 * MenuScale)
						
						opt\EnableUserTracks = UpdateMenuTick(x, y, opt\EnableUserTracks, True)
						
						If opt\EnableUserTracks
							y = y + (30 * MenuScale)
							
							opt\UserTrackMode = UpdateMenuTick(x, y, opt\UserTrackMode)
							
							UpdateMenuButton(x - (270 * MenuScale), y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale, GetLocalString("options", "scantracks"), Font_Default, False, True)
							
							y = y + (40 * MenuScale)
						EndIf
						
						y = y + (30 * MenuScale)
						
						Local PrevEnableSubtitles% = opt\EnableSubtitles
						Local PrevOverrideSubColor% = opt\OverrideSubColor
						
						opt\EnableSubtitles = UpdateMenuTick(x, y, opt\EnableSubtitles)
						If PrevEnableSubtitles <> opt\EnableSubtitles
							If opt\EnableSubtitles Then ClearSubtitles()
						EndIf
						
						If opt\EnableSubtitles
							y = y + (30 * MenuScale)
							
							opt\OverrideSubColor = UpdateMenuTick(x, y, opt\OverrideSubColor)
						EndIf
						
						If PrevEnableSubtitles Lor PrevOverrideSubColor Then ShouldDeleteGadgets = (PrevEnableSubtitles <> opt\EnableSubtitles) Lor (PrevOverrideSubColor <> opt\OverrideSubColor)
						
						If opt\EnableSubtitles And opt\OverrideSubColor
							y = y + (35 * MenuScale)
							
							UpdateMenuPalette(x - (43 * MenuScale), y + (5 * MenuScale))
							
							y = y + (30 * MenuScale)
							
							opt\SubColorR = Min(UpdateMenuInputBox(x - (115 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorR)), Font_Default, 14, 3), 255.0)
							
							y = y + (30 * MenuScale)
							
							opt\SubColorG = Min(UpdateMenuInputBox(x - (115 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorG)), Font_Default, 15, 3), 255.0)
							
							y = y + (30 * MenuScale)
							
							opt\SubColorB = Min(UpdateMenuInputBox(x - (115 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorB)), Font_Default, 16, 3), 255.0)
						EndIf
						;[End Block]
					Case MenuTab_Options_Controls
						;[Block]
						opt\MouseSensitivity = (UpdateMenuSlideBar(x, y, 100 * MenuScale, (opt\MouseSensitivity + 0.5) * 100.0, 1) / 100.0) - 0.5
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseX = UpdateMenuTick(x, y, opt\InvertMouseX)
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseY = UpdateMenuTick(x, y, opt\InvertMouseY)
						
						y = y + (40 * MenuScale)
						
						opt\MouseSmoothing = UpdateMenuSlideBar(x, y, 100 * MenuScale, (opt\MouseSmoothing) * 50.0, 2) / 50.0
						
						y = y + (80 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, 210.0)], Font_Default, 3)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, 210.0)], Font_Default, 4)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, 210.0)], Font_Default, 5)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, 210.0)], Font_Default, 6)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, 210.0)], Font_Default, 7)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, 210.0)], Font_Default, 8)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, 210.0)], Font_Default, 9)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, 210.0)], Font_Default, 10)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, 210.0)], Font_Default, 11)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, 210.0)], Font_Default, 13)
						
						If opt\CanOpenConsole
							y = y + (20 * MenuScale)
							
							UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, 210.0)], Font_Default, 12)
						EndIf
						
						Local TempKey%
						
						For i = 0 To 227
							If KeyHit(i)
								TempKey = i
								Exit
							EndIf
						Next
						If TempKey <> 0
							Select SelectedInputBox
								Case 3
									;[Block]
									key\MOVEMENT_UP = TempKey
									;[End Block]
								Case 4
									;[Block]
									key\MOVEMENT_LEFT = TempKey
									;[End Block]
								Case 5
									;[Block]
									key\MOVEMENT_DOWN = TempKey
									;[End Block]
								Case 6
									;[Block]
									key\MOVEMENT_RIGHT = TempKey
									;[End Block]
								Case 7
									;[Block]
									key\SPRINT = TempKey
									;[End Block]
								Case 8
									;[Block]
									key\CROUCH = TempKey
									;[End Block]
								Case 9
									;[Block]
									key\BLINK = TempKey
									;[End Block]
								Case 10
									;[Block]
									key\INVENTORY = TempKey
									;[End Block]
								Case 11
									;[Block]
									key\SAVE = TempKey
									;[End Block]
								Case 12
									;[Block]
									key\CONSOLE = TempKey
									;[End Block]
								Case 13
									;[Block]
									key\SCREENSHOT = TempKey
									;[End Block]
							End Select
							SelectedInputBox = 0
						EndIf
						;[End Block]
					Case MenuTab_Options_Advanced
						;[Block]
						opt\HUDEnabled = UpdateMenuTick(x, y, opt\HUDEnabled)
						
						y = y + (30 * MenuScale)
						
						Local PrevCanOpenConsole% = opt\CanOpenConsole
						
						opt\CanOpenConsole = UpdateMenuTick(x, y, opt\CanOpenConsole)
						
						If PrevCanOpenConsole Then ShouldDeleteGadgets = (PrevCanOpenConsole <> opt\CanOpenConsole)
						
						y = y + (30 * MenuScale)
						
						If opt\CanOpenConsole Then opt\ConsoleOpening = UpdateMenuTick(x, y, opt\ConsoleOpening)
						
						y = y + (30 * MenuScale)
						
						opt\AchvMsgEnabled = UpdateMenuTick(x, y, opt\AchvMsgEnabled)
						
						y = y + (30 * MenuScale)
						
						opt\AutoSaveEnabled = UpdateMenuTick(x, y, opt\AutoSaveEnabled, SelectedDifficulty\SaveType <> SAVE_ANYWHERE)
						
						y = y + (30 * MenuScale)
						
						opt\TextShadow = UpdateMenuTick(x, y, opt\TextShadow)
						
						y = y + (30 * MenuScale)
						
						opt\ShowFPS = UpdateMenuTick(x, y, opt\ShowFPS)
						
						y = y + (30 * MenuScale)
						
						Local PrevCurrFrameLimit% = opt\CurrFrameLimit > 0.0
						
						If UpdateMenuTick(x, y, opt\CurrFrameLimit > 0.0)
							opt\CurrFrameLimit = UpdateMenuSlideBar(x - (120 * MenuScale), y + (40 * MenuScale), 100 * MenuScale, opt\CurrFrameLimit * 99.0, 1) / 99.0
							opt\CurrFrameLimit = Max(opt\CurrFrameLimit, 0.01)
							opt\FrameLimit = 19 + (opt\CurrFrameLimit * 100.0)
						Else
							opt\CurrFrameLimit = 0.0
							opt\FrameLimit = 0
						EndIf
						
						If PrevCurrFrameLimit Then ShouldDeleteGadgets = (PrevCurrFrameLimit <> opt\CurrFrameLimit)
						
						y = y + (80 * MenuScale)
						
						opt\SmoothBars = UpdateMenuTick(x, y, opt\SmoothBars)
						
						y = y + (30 * MenuScale)
						
						opt\PlayStartup = UpdateMenuTick(x, y, opt\PlayStartup)
						
						y = y + (30 * MenuScale)
						
						opt\LauncherEnabled = UpdateMenuTick(x, y, opt\LauncherEnabled)
						
						y = y + (40 * MenuScale)
						
						UpdateMenuButton(x - (270 * MenuScale), y, 195 * MenuScale, 30 * MenuScale, GetLocalString("options", "reset"), Font_Default, False, True)
						;[End Block]
				End Select
			EndIf
		ElseIf igm\AchievementsMenu <= 0 And igm\OptionsMenu <= 0 And igm\QuitMenu > 0
			Local QuitButton% = 85
			
			If SelectedDifficulty\SaveType = SAVE_ON_QUIT Lor SelectedDifficulty\SaveType = SAVE_ANYWHERE
				If CanSave = 3
					QuitButton = 160
					If UpdateMenuButton(x, y + (85 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "savequit"), Font_Default_Big)
						me\DropSpeed = 0.0
						SaveGame(CurrSave\Name)
						NullGame()
						CurrSave = Null
						ResetInput()
						Return
					EndIf
				EndIf
			EndIf
			
			If UpdateMenuButton(x, y + (QuitButton * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quit"), Font_Default_Big)
				NullGame()
				CurrSave = Null
				ResetInput()
				Return
			EndIf
			
			If UpdateMenuButton(x + (101 * MenuScale), y + 385 * MenuScale, 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
				igm\AchievementsMenu = 0
				igm\OptionsMenu = 0
				igm\QuitMenu = 0
				ResetInput()
				ShouldDeleteGadgets = True
			EndIf
		ElseIf igm\AchievementsMenu > 0 And igm\OptionsMenu <= 0 And igm\QuitMenu <= 0
			If UpdateMenuButton(x + (101 * MenuScale), y + 345 * MenuScale, 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
				igm\AchievementsMenu = 0
				igm\OptionsMenu = 0
				igm\QuitMenu = 0
				ResetInput()
				ShouldDeleteGadgets = True
			EndIf
			
			If igm\AchievementsMenu > 0
				If igm\AchievementsMenu <= Floor(Float(MaxAchievements - 1) / 12.0)
					If UpdateMenuButton(x + (341 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, ">", Font_Default_Big)
						igm\AchievementsMenu = igm\AchievementsMenu + 1
						ShouldDeleteGadgets = True
					EndIf
				Else
					UpdateMenuButton(x + (341 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, ">", Font_Default_Big, False, True)
				EndIf
				If igm\AchievementsMenu > 1
					If UpdateMenuButton(x + (31 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, "<", Font_Default_Big)
						igm\AchievementsMenu = igm\AchievementsMenu - 1
						ShouldDeleteGadgets = True
					EndIf
				Else
					UpdateMenuButton(x + (31 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, "<", Font_Default_Big, False, True)
				EndIf
			EndIf
		Else
			y = y + (10 * MenuScale)
			
			If (Not me\Terminated) Lor me\SelectedEnding <> - 1
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "resume"), Font_Default_Big, True)
					ResumeSounds()
					StopMouseMovement()
					DeleteMenuGadgets()
					MenuOpen = False
					Return
				EndIf
				
				y = y + (75 * MenuScale)
				
				If SelectedDifficulty\SaveType <> NO_SAVES
					If GameSaved
						If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big)
							RenderLoading(0, GetLocalString("loading", "files"))
							
							KillSounds()
							LoadGameQuick(CurrSave\Name)
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							UpdateRender()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0
									CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)] = Max(CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)], 1.0)
									If x < 4.0 And z < 4.0
										If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)] = MapGrid_Tile
									EndIf
								EndIf
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							fps\PrevTime = MilliSecs()
							
							ResetInput()
							MenuOpen = False
							Return
						EndIf
					Else
						UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big, False, True)
					EndIf
					y = y + (75 * MenuScale)
				EndIf
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "achievements"), Font_Default_Big)
					igm\AchievementsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "options"), Font_Default_Big)
					igm\OptionsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quit"), Font_Default_Big)
					igm\QuitMenu = 1
					ShouldDeleteGadgets = True
				EndIf
			Else
				y = y + (75 * MenuScale)
				
				If SelectedDifficulty\SaveType <> NO_SAVES
					If GameSaved
						If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big)
							RenderLoading(0, GetLocalString("loading", "files"))
							
							KillSounds()
							LoadGameQuick(CurrSave\Name)
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							UpdateRender()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0
									CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)] = Max(CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)], 1.0)
									If x < 4.0 And z < 4.0
										If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										CurrMapGrid\Found[Floor(EntityX(r\OBJ) / RoomSpacing) + (Floor(EntityZ(r\OBJ) / RoomSpacing) * MapGridSize)] = MapGrid_Tile
									EndIf
								EndIf
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							fps\PrevTime = MilliSecs()
							
							ResetInput()
							MenuOpen = False
							Return
						EndIf
					Else
						UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big, False, True)
					EndIf
					y = y + (75 * MenuScale)
				EndIf
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quitmenu"), Font_Default_Big)
					NullGame()
					CurrSave = Null
					ResetInput()
					Return
				EndIf
			EndIf
		EndIf
	EndIf
	
	CatchErrors("Uncaught: UpdateMenu()")
End Function

Function RenderMenu%()
	CatchErrors("RenderMenu()")
	
	Local x%, y%, Width%, Height%, i%
	Local TempStr$
	
	If (Not InFocus()) ; ~ Game is out of focus then pause the game
		MenuOpen = True
		PauseSounds()
		Delay(1000) ; ~ Reduce the CPU take while game is not in focus
	EndIf
	If MenuOpen
		Width = ImageWidth(t\ImageID[0])
		Height = ImageHeight(t\ImageID[0])
		x = mo\Viewport_Center_X - (Width / 2)
		y = mo\Viewport_Center_Y - (Height / 2)
		
		If (Not OnPalette)
			ShowPointer()
		Else
			HidePointer()
		EndIf
		
		DrawBlock(t\ImageID[0], x, y)
		
		Color(255, 255, 255)
		
		If igm\AchievementsMenu > 0
			TempStr = GetLocalString("menu", "achievements")
		ElseIf igm\OptionsMenu > 0
			If igm\OptionsMenu = 1
				TempStr = GetLocalString("menu", "options")
			ElseIf igm\OptionsMenu = MenuTab_Options_Graphics
				TempStr = GetLocalString("options", "grap")
			ElseIf igm\OptionsMenu = MenuTab_Options_Audio
				TempStr = GetLocalString("options", "audio")
			ElseIf igm\OptionsMenu = MenuTab_Options_Controls
				TempStr = GetLocalString("options", "ctrl")
			ElseIf igm\OptionsMenu = MenuTab_Options_Advanced
				TempStr = GetLocalString("options", "avc")
			EndIf
		ElseIf igm\QuitMenu > 0
			TempStr = GetLocalString("menu", "quit?")
		ElseIf (Not me\Terminated) Lor me\SelectedEnding <> -1
			TempStr = GetLocalString("menu", "paused")
		Else
			TempStr = GetLocalString("menu", "died")
		EndIf
		SetFontEx(fo\FontID[Font_Default_Big])
		TextEx(x + (Width / 2) + (47 * MenuScale), y + (48 * MenuScale), TempStr, True, True)
		SetFontEx(fo\FontID[Font_Default])
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		Local AchvXIMG% = x + (22 * MenuScale)
		Local Scale# = opt\GraphicHeight / 768.0
		Local SeparationConst% = 76 * Scale
		
		If igm\AchievementsMenu <= 0 And igm\OptionsMenu > 0 And igm\QuitMenu <= 0
			If igm\OptionsMenu > 1
				Local tX# = mo\Viewport_Center_X + (Width / 2)
				Local tY# = y
				Local tW# = 400.0 * MenuScale
				Local tH# = 150.0 * MenuScale
				
				Color(255, 255, 255)
				Select igm\OptionsMenu
					Case MenuTab_Options_Graphics
						;[Block]
						Color(100, 100, 100)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bump"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_BumpMapping)
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vsync"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VSync)
						
						y = y + (30 * MenuScale)
						
						Color(255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)))
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "antialias"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AntiAliasing)
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "lights"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_RoomLights)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "gamma"))
						If (MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ScreenGamma, opt\ScreenGamma)
						
						y = y + (45 * MenuScale)
						
						TextEx(x, y, GetLocalString("options", "particle"))
						If (MouseOn(x + (270 * MenuScale), y - (8 * MenuScale), 114 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParticleAmount, opt\ParticleAmount)
						
						y = y + (45 * MenuScale)
						
						TextEx(x, y, GetLocalString("options", "lod"))
						If (MouseOn(x + (270 * MenuScale), y - (8 * MenuScale), 114 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH + 100 * MenuScale, Tooltip_TextureLODBias)
						
						y = y + (35 * MenuScale)
						
						Color(100, 100, 100)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vram"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SaveTexturesInVRAM)
						
						y = y + (40 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fov"))
						Color(255, 255, 0)
						If (MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 4 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FOV)
						
						y = y + (45 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y, GetLocalString("options", "filter"))
						If (MouseOn(x + (270 * MenuScale), y - (8 * MenuScale), 114 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 5 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AnisotropicFiltering)
						
						y = y + (35 * MenuScale)
						
						Color(100, 100, 100)
						If opt\Atmosphere
							TempStr = GetLocalString("options", "atmo.bright")
						Else
							TempStr = GetLocalString("options", "atmo.dark")
						EndIf
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "atmo") + TempStr)
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Atmosphere)
						
						y = y + (45 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y, GetLocalString("options", "screnderinterval"))
						If (MouseOn(x + (270 * MenuScale), y - (8 * MenuScale), 114 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 17 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SecurityCamRenderInterval)
						;[End Block]
					Case MenuTab_Options_Audio
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mastervolume"))
						If (MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MasterVolume, opt\PrevMasterVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "musicvolume"))
						If (MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MusicVolume, opt\MusicVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "soundvolume"))
						If (MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SoundVolume, opt\SFXVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "voicevolume"))
						If (MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 18 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VoiceVolume, opt\VoiceVolume)
						
						y = y + (40 * MenuScale)
						
						Color(100, 100, 100)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "autorelease"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, Tooltip_SoundAutoRelease)
						
						y = y + (30 * MenuScale)
						
						Color(100, 100, 100)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "enabletracks"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracks)
						
						If opt\EnableUserTracks
							y = y + (30 * MenuScale)
							
							Color(255, 255, 255)
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "trackmode"))
							If opt\UserTrackMode
								TempStr = GetLocalString("options", "track.repeat")
							Else
								TempStr = GetLocalString("options", "track.random")
							EndIf
							TextEx(x + (310 * MenuScale), y + (5 * MenuScale), TempStr)
							If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracksMode)
							If MouseOn(x, y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTrackScan)
							
							y = y + (40 * MenuScale)
						EndIf
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Subtitles)
						
						If opt\EnableSubtitles
							y = y + (30 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color"))
							
							y = y + (5 * MenuScale)
							
							If MouseOn(x + (210 * MenuScale), y, 147 * MenuScale, 147 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
							
							If opt\OverrideSubColor
								y = y + (60 * MenuScale)
								
								TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.red"))
								If MouseOn(x + (105 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
								
								y = y + (30 * MenuScale)
								
								TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.green"))
								If MouseOn(x + (105 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
								
								y = y + (30 * MenuScale)
								
								TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.blue"))
								If MouseOn(x + (105 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
							EndIf
						EndIf
						;[End Block]
					Case MenuTab_Options_Controls
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesensitive"))
						If (MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSensitivity, opt\MouseSensitivity)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "invertx"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertX)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "inverty"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertY)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesmooth"))
						If (MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSmoothing, opt\MouseSmoothing)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("menu", "controlconfig"))
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.forward"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.left"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.backward"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.right"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.sprint"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.crouch"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.blink"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.inv"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.save"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.screenshot"))
						
						If opt\CanOpenConsole
							y = y + (20 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.console"))
						EndIf
						
						If MouseOn(x, y - ((180 + (20 * opt\CanOpenConsole)) * MenuScale), 380 * MenuScale, ((200 + (20 * opt\CanOpenConsole)) * MenuScale)) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ControlConfiguration)
						;[End Block]
					Case MenuTab_Options_Advanced
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "hud"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HUD)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "console"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Console)
						
						y = y + (30 * MenuScale)
						
						If opt\CanOpenConsole
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "error"))
							If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ConsoleOnError)
						EndIf
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "achipop"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AchievementPopups)
						
						y = y + (30 * MenuScale)
						
						Color(255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)), 255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)), 255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)))
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "save"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AutoSave)
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "txtshadow"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_TextShadow)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fps"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FPS)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "frame"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
						If opt\CurrFrameLimit > 0.0
							Color(255, 255, 0)
							TextEx(x, y + (45 * MenuScale), opt\FrameLimit + " FPS")
							If (MouseOn(x + (150 * MenuScale), y + (40 * MenuScale), 114 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
						EndIf
						
						y = y + (80 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bar"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SmoothBars)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "startvideo"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_StartupVideos)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "launcher"))
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Launcher)
						
						y = y + (40 * MenuScale)
						
						If MouseOn(x, y, 195 * MenuScale, 30 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ResetOptions)
						;[End Block]
				End Select
			EndIf
		ElseIf igm\AchievementsMenu <= 0 And igm\OptionsMenu <= 0 And igm\QuitMenu > 0
			; ~ Just save this line, ok?
		ElseIf igm\AchievementsMenu > 0 And igm\OptionsMenu <= 0 And igm\QuitMenu <= 0
			If igm\AchievementsMenu > 0
				For i = 0 To 11
					If i + ((igm\AchievementsMenu - 1) * 12) < MaxAchievements
						RenderAchvIMG(AchvXIMG, y + ((i / 4) * 120 * MenuScale), i + ((igm\AchievementsMenu - 1) * 12))
					Else
						Exit
					EndIf
				Next
				For i = 0 To 11
					If i + ((igm\AchievementsMenu - 1) * 12) < MaxAchievements
						If MouseOn(AchvXIMG + ((i Mod 4) * SeparationConst), y + ((i / 4) * 120 * MenuScale), 64 * Scale, 64 * Scale)
							AchievementTooltip(i + ((igm\AchievementsMenu - 1) * 12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
			EndIf
		Else
			SetFontEx(fo\FontID[Font_Default])
			TextEx(x, y, GetLocalString("menu", "new.diff") + SelectedDifficulty\Name)
			If CurrSave = Null
				TempStr = GetLocalString("menu", "dataredacted")
			Else
				TempStr = ConvertToUTF8(CurrSave\Name)
			EndIf
			TextEx(x, y + (20 * MenuScale), Format(GetLocalString("menu", "save"), TempStr))
			
			If SelectedCustomMap = Null
				TempStr = GetLocalString("menu", "new.seed") + RandomSeed
			Else
				If Len(ConvertToUTF8(SelectedCustomMap\Name)) > 15
					TempStr = GetLocalString("menu", "new.map") + Left(ConvertToUTF8(SelectedCustomMap\Name), 14) + "..."
				Else
					TempStr = GetLocalString("menu", "new.map") + ConvertToUTF8(SelectedCustomMap\Name)
				EndIf
			EndIf
			TextEx(x, y + (40 * MenuScale), TempStr)
			
			If me\Terminated And me\SelectedEnding = -1
				y = y + (175 * MenuScale)
				If SelectedDifficulty\SaveType <> NO_SAVES
					y = y + (75 * MenuScale)
				EndIf
				SetFontEx(fo\FontID[Font_Default])
				RowText(msg\DeathMsg, x, y, 430 * MenuScale, 600 * MenuScale)
			EndIf
		EndIf
		
		RenderMenuButtons()
		RenderMenuPalettes()
		RenderMenuTicks()
		RenderMenuInputBoxes()
		RenderMenuSlideBars()
		RenderMenuSliders()
		
		RenderCursor()
	EndIf
	
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderMenu()")
End Function

; ~ Endings ID Constants
;[Block]
Const Ending_A1% = 0
Const Ending_A2% = 1
Const Ending_B1% = 2
Const Ending_B2% = 3
;[End Block]

Function UpdateEnding%()
	Local x%, y%, Width%, Height%, i%
	
	fps\Factor[0] = 0.0
	If me\EndingTimer > -2000.0
		me\EndingTimer = Max(me\EndingTimer - fps\Factor[1], -1111.0)
	Else
		me\EndingTimer = me\EndingTimer - fps\Factor[1]
	EndIf
	
	GiveAchievement(Achv055)
	If ((Not UsedConsole) Lor opt\DebugMode) And SelectedCustomMap = Null
		GiveAchievement(AchvConsole)
		If SelectedDifficulty\Name = "Keter" Lor SelectedDifficulty\Name = "Apollyon"
			GiveAchievement(AchvKeter)
			SaveAchievementsFile()
		EndIf
	EndIf
	
	ShouldPlay = 66
	
	If me\EndingTimer < -200.0
		StopBreathSound() : me\Stamina = 100.0
		
		If me\EndingScreen = 0
			me\EndingScreen = LoadImage_Strict("GFX\Menu\ending_screen.png")
			me\EndingScreen = ScaleImage2(me\EndingScreen, MenuScale, MenuScale)
			
			ShouldPlay = 22
			opt\CurrMusicVolume = opt\MusicVolume
			StopStream_Strict(MusicCHN) : MusicCHN = 0
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music[22] + ".ogg", opt\CurrMusicVolume * opt\MasterVolume, 0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict(LightSFX)
		EndIf
		
		If me\EndingTimer > -700.0
			If me\EndingTimer + fps\Factor[1] > -450.0 And me\EndingTimer <= -450.0 Then PlaySound_Strict(LoadTempSound("SFX\Ending\Ending" + (me\SelectedEnding + 1) + ".ogg"), True)
		Else
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0
				If igm\AchievementsMenu =< 0
					Width = ImageWidth(t\ImageID[0])
					Height = ImageHeight(t\ImageID[0])
					x = mo\Viewport_Center_X - (Width / 2)
					y = mo\Viewport_Center_Y - (Height / 2)
					x = x + (132 * MenuScale)
					y = y + (432 * MenuScale)
					
					If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "achievements"), Font_Default_Big)
						igm\AchievementsMenu = 1
						ShouldDeleteGadgets = True
					EndIf
					
					y = y + 75 * MenuScale
					
					If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "mainmenu"), Font_Default_Big)
						ShouldPlay = 23
						NowPlaying = ShouldPlay
						For i = 0 To 8 Step 2
							If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
							If TempSounds[i + 1] <> 0 Then FreeSound_Strict(TempSounds[i + 1]) : TempSounds[i + 1] = 0
						Next
						StopStream_Strict(MusicCHN) : MusicCHN = 0
						MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
						SetStreamVolume_Strict(MusicCHN, opt\MusicVolume * opt\MasterVolume)
						me\EndingTimer = -2000.0
						ShouldDeleteGadgets = True
						ResetInput()
						InitCredits()
					EndIf
				Else
					ShouldPlay = 22
					UpdateMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer <= -2000.0
				ShouldPlay = 23
				UpdateCredits()
			EndIf
		EndIf
	EndIf
End Function

Function RenderEnding%()
	ShowPointer()
	
	Local itt.ItemTemplates, r.Rooms
	Local x%, y%, Width%, Height%, i%
	
	Select me\SelectedEnding
		Case Ending_A1, Ending_B2
			;[Block]
			ClsColor(Max(255.0 + (me\EndingTimer) * 2.8, 0.0), Max(255.0 + (me\EndingTimer) * 2.8, 0.0), Max(255.0 + (me\EndingTimer) * 2.8, 0.0))
			;[End Block]
		Default
			;[Block]
			ClsColor(0, 0, 0)
			;[End Block]
	End Select
	
	Cls()
	
	If me\EndingTimer < -200.0
		If me\EndingTimer > -700.0
			If Rand(150) < Min((Abs(me\EndingTimer) - 200.0), 155.0)
				DrawBlock(me\EndingScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
			Else
				Color(0, 0, 0)
				Rect(100, 100, opt\GraphicWidth - 200, opt\GraphicHeight - 200)
				Color(255, 255, 255)
			EndIf
		Else
			DrawBlock(me\EndingScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
			
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0
				Width = ImageWidth(t\ImageID[0])
				Height = ImageHeight(t\ImageID[0])
				x = mo\Viewport_Center_X - (Width / 2)
				y = mo\Viewport_Center_Y - (Height / 2)
				
				DrawBlock(t\ImageID[0], x, y)
				
				Color(255, 255, 255)
				SetFontEx(fo\FontID[Font_Default_Big])
				TextEx(x + (Width / 2) + (47 * MenuScale), y + (48 * MenuScale), GetLocalString("menu", "end"), True, True)
				SetFontEx(fo\FontID[Font_Default])
				
				If igm\AchievementsMenu =< 0
					x = x + (132 * MenuScale)
					y = y + (122 * MenuScale)
					
					Local RoomAmount% = 0, RoomsFound% = 0
					
					For r.Rooms = Each Rooms
						Local RID% = r\RoomTemplate\RoomID
						
						If RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499
							RoomAmount = RoomAmount + 1
							RoomsFound = RoomsFound + r\Found
						EndIf
					Next
					
					Local DocAmount% = 0, DocsFound% = 0
					
					For itt.ItemTemplates = Each ItemTemplates
						If itt\TempName = "paper"
							DocAmount = DocAmount + 1
							DocsFound = DocsFound + itt\Found
						EndIf
					Next
					
					Local SCPsEncountered% = 1
					
					For i = Achv005 To Achv1499
						SCPsEncountered = SCPsEncountered + achv\Achievement[i]
					Next
					
					Local AchievementsUnlocked% = 0
					
					For i = 0 To MaxAchievements - 1
						AchievementsUnlocked = AchievementsUnlocked + achv\Achievement[i]
					Next
					
					Local EscapeSeconds% = EscapeTimer Mod 60
					Local EscapeMinutes% = Floor(EscapeTimer / 60)
					Local EscapeHours% = Floor(EscapeMinutes / 60)
					
					EscapeMinutes = EscapeMinutes - (EscapeHours * 60)
					
					TextEx(x, y, Format(GetLocalString("menu", "end.scps"), SCPsEncountered))
					TextEx(x, y + (20 * MenuScale), Format(Format(GetLocalString("menu", "end.achi"), AchievementsUnlocked, "{0}"), MaxAchievements, "{1}"))
					TextEx(x, y + (40 * MenuScale), Format(Format(GetLocalString("menu", "end.room"), RoomsFound, "{0}"), RoomAmount, "{1}"))
					TextEx(x, y + (60 * MenuScale), Format(Format(GetLocalString("menu", "end.doc"), DocsFound, "{0}"), DocAmount, "{1}"))
					TextEx(x, y + (80 * MenuScale), Format(GetLocalString("menu", "end.914"), me\RefinedItems))
					TextEx(x, y + (100 * MenuScale), Format(Format(Format(GetLocalString("menu", "end.escape"), EscapeHours, "{0}"), EscapeMinutes, "{1}"), EscapeSeconds, "{2}"))
				Else
					RenderMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer <= -2000.0
				RenderCredits()
			EndIf
		EndIf
	EndIf
	
	RenderMenuButtons()
	
	RenderCursor()
	
	SetFontEx(fo\FontID[Font_Default])
End Function

Type CreditsLine
	Field Txt$
	Field ID%
	Field Stay%
End Type

Function InitCredits%()
	Local cl.CreditsLine
	Local File% = OpenFile_Strict("Credits.txt")
	Local l$
	
	fo\FontID[Font_Credits] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Credits", "File"), GetFileLocalString(FontsFile, "Credits", "Size"))
	fo\FontID[Font_Credits_Big] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Credits_Big", "File"), GetFileLocalString(FontsFile, "Credits_Big", "Size"))
	
	If me\CreditsScreen = 0
		me\CreditsScreen = LoadImage_Strict("GFX\Menu\credits_screen.png")
		me\CreditsScreen = ScaleImage2(me\CreditsScreen, MenuScale, MenuScale)
	EndIf
	
	Repeat
		l = ReadLine(File)
		cl.CreditsLine = New CreditsLine
		cl\Txt = l
	Until Eof(File)
	
	Delete First CreditsLine
	me\CreditsTimer = 0.0
End Function

Function UpdateCredits%()
	Local cl.CreditsLine, LastCreditLine.CreditsLine
	Local Credits_Y# = ((me\EndingTimer + 2000.0) / 2) + (opt\GraphicHeight + 10.0)
	Local ID%
	Local EndLinesAmount%
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "/" Then LastCreditLine = Before(cl)
		If LastCreditLine <> Null Then cl\Stay = (cl\ID > LastCreditLine\ID)
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		me\CreditsTimer = me\CreditsTimer + (0.5 * fps\Factor[1])
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			; ~ Just save this line, ok?
		ElseIf me\CreditsTimer >= 255.0
			If me\CreditsTimer > 500.0 Then me\CreditsTimer = -255.0
		Else
			If me\CreditsTimer >= -1.0 Then me\CreditsTimer = -1.0
		EndIf
	EndIf
	
	If GetKey() <> 0 Lor MouseHit(1) Then me\CreditsTimer = -1.0
	
	If me\CreditsTimer = -1.0
		Delete Each CreditsLine
		NullGame(False)
		StopStream_Strict(MusicCHN) : MusicCHN = 0
		ShouldPlay = 20
		CurrSave = Null
		ResetLoadingTextColor()
		ResetInput()
		Return
	EndIf
End Function

Function RenderCredits%()
	Local cl.CreditsLine, LastCreditLine.CreditsLine
	Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (opt\GraphicHeight + 10.0)
	Local ID%
	Local EndLinesAmount%
	
	Cls()
	
	If Rand(300) > 1 Then DrawBlock(me\CreditsScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	Color(255, 255, 255)
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "*"
			SetFontEx(fo\FontID[Font_Credits_Big])
			If (Not cl\Stay) Then TextEx(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
		ElseIf Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		Else
			SetFontEx(fo\FontID[Font_Credits])
			If (Not cl\Stay) Then TextEx(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), cl\Txt, True)
		EndIf
		If LastCreditLine <> Null Then cl\Stay = (cl\ID > LastCreditLine\ID)
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			Color(Max(Min(me\CreditsTimer, 255.0), 0.0), Max(Min(me\CreditsTimer, 255.0), 0.0), Max(Min(me\CreditsTimer, 255.0), 0.0))
		ElseIf me\CreditsTimer >= 255.0
			Color(255, 255, 255)
		Else
			Color(Max(Min(-me\CreditsTimer, 255.0), 0.0), Max(Min(-me\CreditsTimer, 255.0), 0.0), Max(Min(-me\CreditsTimer, 255.0), 0.0))
		EndIf
	EndIf
	If me\CreditsTimer <> 0.0
		For cl.CreditsLine = Each CreditsLine
			If cl\Stay
				SetFontEx(fo\FontID[Font_Credits])
				If Left(cl\Txt, 1) = "/"
					TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y + (EndLinesAmount / 2) + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
				Else
					TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y + (24 * (cl\ID - LastCreditLine\ID) * MenuScale) - ((EndLinesAmount / 2) * 24 * MenuScale), cl\Txt, True)
				EndIf
			EndIf
		Next
	EndIf
	
	RenderLoadingText(20 * MenuScale, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"))
	
	Flip(True)
	
	If me\CreditsTimer = -1.0
		FreeFont(fo\FontID[Font_Credits]) : fo\FontID[Font_Credits] = 0
		FreeFont(fo\FontID[Font_Credits_Big]) : fo\FontID[Font_Credits_Big] = 0
		FreeImage(me\CreditsScreen) : me\CreditsScreen = 0
		FreeImage(me\EndingScreen) : me\EndingScreen = 0
		Return
	EndIf
End Function

Global MTFTimer#
Global MTFCameraCheckTimer#
Global MTFCameraCheckDetected%

Function UpdateMTF%()
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	
	If RID = r_gate_a_entrance Then Return
	
	Local r.Rooms, n.NPCs
	Local Dist#, i%
	
	If MTFTimer = 0.0
		If Rand(200) = 1 And RID <> r_dimension_1499
			Local entrance.Rooms = Null
			
			For r.Rooms = Each Rooms
				If r\RoomTemplate\RoomID = r_gate_a_entrance 
					entrance = r
					Exit
				EndIf
			Next
			
			If entrance <> Null
				If Abs(EntityZ(entrance\OBJ) - EntityZ(me\Collider)) < 36.0
					If me\Zone = 2
						If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
						
						MTFTimer = fps\Factor[0]
						
						For i = 0 To 2
							n.NPCs = CreateNPC(NPCTypeMTF, EntityX(entrance\RoomCenter, True) + 0.3 * (i - 1), 0.6, EntityZ(entrance\RoomCenter, True))
						Next
						If i = 0 Then n_I\MTFLeader = n
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		If MTFTimer <= 70.0 * 120.0
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 70.0 * 120.0 And MTFTimer < 10000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			MTFTimer = 10000.0
		ElseIf MTFTimer >= 10000.0 And MTFTimer <= 10000.0 + (70.0 * 120.0)
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 10000.0 + (70.0 * 120.0) And MTFTimer < 20000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			MTFTimer = 20000.0
		ElseIf MTFTimer >= 20000.0 And MTFTimer <= 20000.0 + (70.0 * 60.0)
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 20000.0 + (70.0 * 60.0) And MTFTimer < 25000.0
			If PlayerInReachableRoom()
				Local Temp% = False
				
				; ~ If the player has an SCP in their inventory play special voice line
				For i = 0 To MaxItemAmount - 1
					If Inventory(i) <> Null
						If (Left(Inventory(i)\ItemTemplate\Name, 4) = "SCP-") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-035") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-093")
							Temp = True
							Exit
						EndIf
					EndIf
				Next
				If Temp
					PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncPossession.ogg")
				Else
					PlayAnnouncement("SFX\Character\MTF\ThreatAnnounc" + Rand(3) + ".ogg")
				EndIf
			EndIf
			MTFTimer = 25000.0
		ElseIf MTFTimer >= 25000.0 And MTFTimer <= 25000.0 + (70.0 * 60.0)
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 25000.0 + (70.0 * 60.0) And MTFTimer < 30000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			MTFTimer = 30000.0
		EndIf
		If n_I\MTFLeader = Null And MTFTimer < 35000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncLost.ogg")
			MTFTimer = 35000.0
		EndIf
	EndIf
End Function

Function UpdateCameraCheck%()
	If MTFCameraCheckTimer > 0.0 And MTFCameraCheckTimer < 70.0 * 90.0
		MTFCameraCheckTimer = MTFCameraCheckTimer + fps\Factor[0]
	ElseIf MTFCameraCheckTimer >= 70.0 * 90.0
		MTFCameraCheckTimer = 0.0
		If (Not me\Detected)
			If MTFCameraCheckDetected
				If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncCameraFound" + Rand(2) + ".ogg")
				me\Detected = True
				MTFCameraCheckTimer = 70.0 * 60.0
			Else
				If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncCameraNoFound.ogg")
			EndIf
		EndIf
		MTFCameraCheckDetected = False
		If MTFCameraCheckTimer = 0.0 Then me\Detected = False
	EndIf
End Function

Function UpdateExplosion%()
	Local p.Particles
	Local i%
	
	; ~ This here is necessary because the SCP-294's drinks with explosion effect didn't worked anymore -- ENDSHN
	If me\ExplosionTimer > 0.0
		me\ExplosionTimer = me\ExplosionTimer + fps\Factor[0]
		If me\ExplosionTimer < 140.0
			If me\ExplosionTimer - fps\Factor[0] < 5.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke1.ogg")
				PlaySound_Strict(ExplosionSFX)
				me\BigCameraShake = 10.0
				me\ExplosionTimer = 5.0
			EndIf
			me\BigCameraShake = CurveValue(me\ExplosionTimer / 60.0, me\BigCameraShake, 50.0)
		Else
			me\BigCameraShake = Min((me\ExplosionTimer / 20.0), 20.0)
			If me\ExplosionTimer - fps\Factor[0] < 140.0
				me\BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke2.ogg")
				PlaySound_Strict(ExplosionSFX)
				For i = 0 To (10 + (10 * (opt\ParticleAmount + 1)))
					p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, EntityX(me\Collider) + Rnd(-0.5, 0.5), EntityY(me\Collider) - Rnd(0.2, 1.5), EntityZ(me\Collider) + Rnd(-0.5, 0.5), Rnd(0.2, 0.6), 0.0, 350.0)
					RotateEntity(p\Pvt, -90.0, 0.0, 0.0, True)
					p\Speed = Rnd(0.05, 0.07)
				Next
			EndIf
			me\LightFlash = Min((me\ExplosionTimer - 140.0) / 10.0, 5.0)
			
			If me\ExplosionTimer > 160.0 Then me\Terminated = True
			If me\ExplosionTimer > 500.0 Then me\ExplosionTimer = 0.0
			
			; ~ A dirty workaround to prevent the collider from falling down into the facility once the nuke goes off, causing the UpdateEvents() function to be called again and crashing the game
			PositionEntity(me\Collider, EntityX(me\Collider), 200.0, EntityZ(me\Collider))
		EndIf
	EndIf
End Function

Function UpdateVomit%()
	CatchErrors("UpdateVomit()")
	
	Local de.Decals
	Local Pvt%
	Local FPSFactorEx# = fps\Factor[0] / 70.0
	
	If me\CameraShakeTimer > 0.0
		me\CameraShakeTimer = Max(me\CameraShakeTimer - FPSFactorEx, 0.0)
		me\CameraShake = 2.0
	EndIf
	
	If me\VomitTimer > 0.0
		me\VomitTimer = me\VomitTimer - FPSFactorEx
		
		If (MilliSec Mod 1600) < Rand(200, 400)
			If me\BlurTimer = 0.0 Then me\BlurTimer = 70.0 * Rnd(10.0, 20.0)
			me\CameraShake = Rnd(0.0, 2.0)
		EndIf
		
		If Rand(50) = 50 And (MilliSec Mod 4000) < 200 Then PlaySound_Strict(CoughSFX((wi\GasMask > 0) Lor (I_1499\Using > 0) Lor (wi\HazmatSuit > 0), Rand(0, 2)), True)
		
		; ~ Regurgitate when timer is below 10 seconds
		If me\VomitTimer < 10.0 And Rnd(0.0, 500.0 * me\VomitTimer) < 2.0
			If (Not ChannelPlaying(VomitCHN)) And me\Regurgitate = 0
				VomitCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Retch" + Rand(2) + ".ogg"), True)
				me\Regurgitate = MilliSecs() + 50
			EndIf
		EndIf
		
		If me\Regurgitate > MilliSecs() And me\Regurgitate <> 0
			mo\Mouse_Y_Speed_1 = mo\Mouse_Y_Speed_1 + 1.0
		Else
			me\Regurgitate = 0
		EndIf
	ElseIf me\VomitTimer < 0.0 ; ~ Vomit
		me\VomitTimer = me\VomitTimer - FPSFactorEx
		
		If me\VomitTimer > -5.0
			If (MilliSec Mod 400) < 50 Then me\CameraShake = 4.0
			mo\Mouse_X_Speed_1 = 0.0
			MakeMeUnplayable()
		Else
			me\Playable = True
		EndIf
		
		If (Not me\Vomit)
			me\BlurTimer = 70.0 * 40.0
			VomitSFX = LoadSound_Strict("SFX\SCP\294\Vomit.ogg")
			VomitCHN = PlaySound_Strict(VomitSFX, True)
			me\PrevInjuries = me\Injuries
			me\PrevBloodloss = me\Bloodloss
			If (Not me\Crouch) Then SetCrouch(True)
			me\Injuries = 1.5
			me\Bloodloss = 70.0
			me\EyeIrritation = 70.0 * 9.0
			
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(Camera), EntityY(me\Collider) - 0.05, EntityZ(Camera))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(DECAL_BLOOD_4, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, 180.0, 0.0, 0.001, 1.0, 0, 1, 0, Rand(200, 255), 0)
			de\SizeChange = 0.001 : de\MaxSize = 0.6
			EntityParent(de\OBJ, PlayerRoom\OBJ)
			FreeEntity(Pvt) : Pvt = 0
			me\Vomit = True
		EndIf
		
		mo\Mouse_Y_Speed_1 = mo\Mouse_Y_Speed_1 + Max((1.0 + me\VomitTimer / 10.0), 0.0)
		
		If me\VomitTimer < -15.0
			FreeSound_Strict(VomitSFX)
			me\VomitTimer = 0.0
			If (Not me\Terminated) Then PlaySound_Strict(BreathSFX(0, 0), True)
			me\Injuries = me\PrevInjuries
			me\Bloodloss = me\PrevBloodloss
			me\Vomit = False
		EndIf
	EndIf
	
	CatchErrors("Uncaught: UpdateVomit()")
End Function

Global EscapeTimer%
Global EscapeSecondsTimer#

Function UpdateEscapeTimer%()
	Local ev.Events
	
	For ev.Events = Each Events
		If ev\EventID = e_cont1_173_intro
			If ev\room = PlayerRoom
				Return
				Exit
			EndIf
		EndIf
	Next
	
	EscapeSecondsTimer = EscapeSecondsTimer - fps\Factor[0]
	If EscapeSecondsTimer <= 0.0
		EscapeTimer = EscapeTimer + 1
		EscapeSecondsTimer = 70.0
	EndIf
End Function

Type SCP005
	Field ChanceToSpawn%
End Type

Global I_005.SCP005

Type SCP035
	Field Sad%
End Type

Global I_035.SCP035

Type SCP500
	Field Taken%
End Type

Global I_500.SCP500

Type SCP714
	Field Using%
End Type

Global I_714.SCP714

Type SCP008
	Field Timer#
	Field Revert%
End Type

Global I_008.SCP008

Function Update008%()
	Local r.Rooms, e.Events, p.Particles, de.Decals
	Local PrevI008Timer#, i%
	Local TeleportForInfect%
	Local SinValue#
	
	TeleportForInfect = PlayerInReachableRoom()
	If I_008\Timer > 0.0
		If EntityHidden(t\OverlayID[3]) Then ShowEntity(t\OverlayID[3])
		SinValue = Sin(MilliSec / 8.0) + 2.0
		If I_008\Timer < 93.0
			PrevI008Timer = I_008\Timer
			If I_427\Timer < 70.0 * 360.0
				If I_008\Revert
					I_008\Timer = Max(0.0, I_008\Timer - (fps\Factor[0] * 0.01))
				Else
					If (Not I_427\Using) Then I_008\Timer = Min(I_008\Timer + (fps\Factor[0] * 0.002), 100.0)
				EndIf
			EndIf
			
			me\BlurTimer = Max(I_008\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
			
			me\HeartBeatRate = Max(me\HeartBeatRate, 100.0)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, I_008\Timer / 120.0)
			
			EntityAlpha(t\OverlayID[3], Min(PowTwo(I_008\Timer * 0.2) / 1000.0, 0.5) * SinValue)
			
			For i = 0 To 6
				If I_008\Timer > (i * 15.0) + 10.0 And PrevI008Timer <= (i * 15.0) + 10.0
					If (Not I_008\Revert) Then PlaySound_Strict(LoadTempSound("SFX\SCP\008\Voices" + i + ".ogg"), True)
				EndIf
			Next
			
			If I_008\Revert
				If I_008\Timer <= 20.0 And PrevI008Timer > 20.0
					CreateMsg(GetLocalString("msg", "better_2"))
				ElseIf I_008\Timer <= 40.0 And PrevI008Timer > 40.0
					CreateMsg(GetLocalString("msg", "nauseafading"))
				ElseIf I_008\Timer <= 60.0 And PrevI008Timer > 60.0
					CreateMsg(GetLocalString("msg", "headachefading"))
				ElseIf I_008\Timer <= 80.0 And PrevI008Timer > 80.0
					CreateMsg(GetLocalString("msg", "moreener"))
				EndIf
			Else
				If I_008\Timer > 20.0 And PrevI008Timer <= 20.0
					CreateMsg(GetLocalString("msg", "feverish"))
				ElseIf I_008\Timer > 40.0 And PrevI008Timer <= 40.0
					CreateMsg(GetLocalString("msg", "nausea"))
				ElseIf I_008\Timer > 60.0 And PrevI008Timer <= 60.0
					CreateMsg(GetLocalString("msg", "nauseaworse"))
				ElseIf I_008\Timer > 80.0 And PrevI008Timer <= 80.0
					CreateMsg(GetLocalString("msg", "faint"))
				ElseIf I_008\Timer >= 91.5
					me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 91.5), me\BlinkTimer), -10.0)
					me\Zombie = True : MakeMeUnplayable()
					If I_008\Timer >= 92.7 And PrevI008Timer < 92.7
						If TeleportForInfect
							For r.Rooms = Each Rooms
								If r\RoomTemplate\RoomID = r_cont2_008
									PositionEntity(me\Collider, EntityX(r\Objects[8], True), EntityY(r\Objects[8], True), EntityZ(r\Objects[8], True), True)
									ResetEntity(me\Collider)
									r\NPC[0] = CreateNPC(NPCTypeD, EntityX(r\Objects[7], True), EntityY(r\Objects[7], True) + 0.2, EntityZ(r\Objects[7], True))
									PlaySound_Strict(LoadTempSound("SFX\SCP\008\KillScientist1.ogg"), True)
									ChangeNPCTextureID(r\NPC[0], NPC_CLASS_D_VICTIM_008_TEXTURE)
									TeleportToRoom(r)
									Exit
								EndIf
							Next
						EndIf
					EndIf
				EndIf
			EndIf
		Else
			PrevI008Timer = I_008\Timer
			I_008\Timer = Min(I_008\Timer + (fps\Factor[0] * 0.004), 100.0)
			
			If TeleportForInfect
				If I_008\Timer < 94.7
					EntityAlpha(t\OverlayID[3], 0.5 * SinValue)
					me\BlurTimer = 900.0
					
					If I_008\Timer > 94.5 Then me\BlinkTimer = Max(Min((-50.0) * (I_008\Timer - 94.5), me\BlinkTimer), -10.0)
					PointEntity(me\Collider, PlayerRoom\NPC[0]\Collider)
					PointEntity(PlayerRoom\NPC[0]\Collider, me\Collider)
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider, EntityRoll(Camera))
					me\ForceMove = 0.75
					me\Injuries = 2.5
					me\Bloodloss = 0.0
					me\Playable = True
					
					Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 357.0, 381.0, 0.3)
				ElseIf I_008\Timer < 98.5
					EntityAlpha(t\OverlayID[3], 0.5 * SinValue)
					me\BlurTimer = 950.0
					
					me\ForceMove = 0.0
					MakeMeUnplayable()
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider)
					
					If PrevI008Timer < 94.7
						PlayerRoom\NPC[0]\State3 = -1.0 : PlayerRoom\NPC[0]\IsDead = True
						SetNPCFrame(PlayerRoom\NPC[0], 19.0)
						
						PlaySound_Strict(LoadTempSound("SFX\SCP\008\KillScientist2.ogg"), True)
						
						msg\DeathMsg = Format(GetLocalString("death", "0081"), SubjectName)
						
						de.Decals = CreateDecal(DECAL_BLOOD_2, EntityX(PlayerRoom\NPC[0]\Collider), PlayerRoom\y + 544.0 * RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider), 90.0, Rnd(360.0), 0.0, 0.8)
						EntityParent(de\OBJ, PlayerRoom\OBJ)
						
						Kill()
					ElseIf I_008\Timer > 96.0
						me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 96.0), me\BlinkTimer), -10.0)
					Else
						me\Terminated = True
					EndIf
					
					If opt\ParticleAmount > 0
						If Rand(50) = 1
							p.Particles = CreateParticle(PARTICLE_BLOOD, EntityX(PlayerRoom\NPC[0]\Collider), EntityY(PlayerRoom\NPC[0]\Collider), EntityZ(PlayerRoom\NPC[0]\Collider), Rnd(0.05, 0.1), 0.15, 200.0)
							p\Speed = 0.01 : p\SizeChange = 0.01 : p\Alpha = 0.5 : p\AlphaChange = -0.01
							RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
						EndIf
					EndIf
					
					PositionEntity(me\Head, EntityX(PlayerRoom\NPC[0]\Collider, True), EntityY(PlayerRoom\NPC[0]\Collider, True) + 0.65, EntityZ(PlayerRoom\NPC[0]\Collider, True), True)
					SinValue = Sin(MilliSec / 5.0)
					RotateEntity(me\Head, (1.0 + SinValue) * 15.0, PlayerRoom\Angle - 180.0, 0.0, True)
					MoveEntity(me\Head, 0.0, 0.0, -0.4)
					TurnEntity(me\Head, 80.0 + SinValue * 30.0, SinValue * 40.0, 0.0)
				EndIf
			Else
				Local RID% = PlayerRoom\RoomTemplate\RoomID
				
				me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 96.0), me\BlinkTimer), -10.0)
				If RID = r_dimension_1499
					msg\DeathMsg = GetLocalString("death", "14991")
				ElseIf IsPlayerOutsideFacility()
					msg\DeathMsg = Format(GetLocalString("death", "008gate"), SubjectName, "{0}")
					If RID = r_gate_a
						msg\DeathMsg = Format(msg\DeathMsg, "A", "{1}")
					Else
						msg\DeathMsg = Format(msg\DeathMsg, "B", "{1}")
					EndIf
				Else
					msg\DeathMsg = ""
				EndIf
				Kill()
			EndIf
		EndIf
	Else
		I_008\Revert = False
		If (Not EntityHidden(t\OverlayID[3])) Then HideEntity(t\OverlayID[3])
	EndIf
End Function

Type SCP268
	Field Using%
	Field Timer#
	Field InvisibilityOn%
End Type

Global I_268.SCP268

Function Update268%()
    If I_268\Using > 1
		I_268\InvisibilityOn = (I_268\Timer > 0.0)
		If I_268\Using = 3 
            I_268\Timer = Max(I_268\Timer - ((fps\Factor[0] / 1.5) * (1.0 + I_714\Using)), 0.0)
        Else
            I_268\Timer = Max(I_268\Timer - (fps\Factor[0] * (1.0 + I_714\Using)), 0.0)
        EndIf
    Else
        I_268\Timer = Min(I_268\Timer + fps\Factor[0], 600.0)
		I_268\InvisibilityOn = False
    EndIf
End Function 

Type SCP409
	Field Timer#
	Field Revert%
End Type

Global I_409.SCP409

Function Update409%()
	Local PrevI409Timer# = I_409\Timer
	
	If I_409\Timer > 0.0
		If EntityHidden(t\OverlayID[7]) Then ShowEntity(t\OverlayID[7])
		If I_427\Timer < 70.0 * 360.0
			If I_409\Revert
				I_409\Timer = Max(0.0, I_409\Timer - (fps\Factor[0] * 0.01))
			Else
				If (Not I_427\Using) Then I_409\Timer = Min(I_409\Timer + (fps\Factor[0] * 0.004), 100.0)
			EndIf
		EndIf
		EntityAlpha(t\OverlayID[7], Min((PowTwo(I_409\Timer * 0.2)) / 1000.0, 0.5))
		me\BlurTimer = Max(I_409\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
		
		If I_409\Revert
			If I_409\Timer <= 40.0 And PrevI409Timer > 40.0
				CreateMsg(GetLocalString("msg", "409legs_1"))
			ElseIf I_409\Timer <= 55.0 And PrevI409Timer > 55.0
				CreateMsg(GetLocalString("msg", "409abdomen_1"))
			ElseIf I_409\Timer <= 70.0 And PrevI409Timer > 70.0
				CreateMsg(GetLocalString("msg", "409arms_1"))
			ElseIf I_409\Timer <= 85.0 And PrevI409Timer > 85.0
				CreateMsg(GetLocalString("msg", "409head_1"))
			EndIf
		Else
			If I_409\Timer > 40.0 And PrevI409Timer <= 40.0
				CreateMsg(GetLocalString("msg", "409legs_2"))
			ElseIf I_409\Timer > 55.0 And PrevI409Timer <= 55.0
				CreateMsg(GetLocalString("msg", "409abdomen_2"))
			ElseIf I_409\Timer > 70.0 And PrevI409Timer <= 70.0
				CreateMsg(GetLocalString("msg", "409arms_2"))
			ElseIf I_409\Timer > 85.0 And PrevI409Timer <= 85.0
				CreateMsg(GetLocalString("msg", "409head_2"))
			ElseIf I_409\Timer > 93.0 And PrevI409Timer <= 93.0
				If (Not I_409\Revert)
					PlaySound_Strict(DamageSFX[13], True)
					me\Injuries = Max(me\Injuries, 2.0)
				EndIf
			ElseIf I_409\Timer > 94.0
				I_409\Timer = Min(I_409\Timer + (fps\Factor[0] * 0.004), 100.0)
				MakeMeUnplayable()
				me\BlurTimer = 4.0
				me\CameraShake = 3.0
			EndIf
		EndIf
		If I_409\Timer >= 55.0
			me\StaminaEffect = 1.2
			me\StaminaEffectTimer = 1.0
			me\Stamina = Min(me\Stamina, 60.0)
		EndIf
		If I_409\Timer >= 96.92
			msg\DeathMsg = Format(GetLocalString("death", "409"), SubjectName)
			Kill(True)
		EndIf
	Else
		I_409\Revert = False
		If (Not EntityHidden(t\OverlayID[7])) Then HideEntity(t\OverlayID[7])
	EndIf
End Function

Type SCP427
	Field Using%
	Field Timer#
	Field Sound%[2]
	Field SoundCHN%[2]
End Type

Global I_427.SCP427

Function Update427%()
	Local de.Decals, e.Events
	Local i%, Pvt%, TempCHN%
	Local PrevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70.0 * 360.0
		If I_427\Using
			I_427\Timer = I_427\Timer + fps\Factor[0]
			If me\Injuries > 0.0 Then me\Injuries = Max(me\Injuries - (fps\Factor[0] * 0.0005), 0.0)
			If me\Bloodloss > 0.0 And me\Injuries <= 1.0 Then me\Bloodloss = Max(me\Bloodloss - (fps\Factor[0] * 0.001), 0.0)
			If I_008\Timer > 0.0 Then I_008\Timer = Max(I_008\Timer - (fps\Factor[0] * 0.001), 0.0)
			If I_409\Timer > 0.0 Then I_409\Timer = Max(I_409\Timer - (fps\Factor[0] * 0.003), 0.0)
			For i = 0 To 6
				If I_1025\State[i] > 0.0 Then I_1025\State[i] = Max(I_1025\State[i] - (0.001 * fps\Factor[0] * I_1025\State[7]), 0.0)
			Next
			If I_427\Sound[0] = 0 Then I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			If I_427\Timer >= 70.0 * 180.0
				If I_427\Sound[1] = 0 Then I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
			EndIf
			If PrevI427Timer < 70.0 * 60.0 And I_427\Timer >= 70.0 * 60.0
				CreateMsg(GetLocalString("msg", "freshener"))
			ElseIf PrevI427Timer < 70.0 * 180.0 And I_427\Timer >= 70.0 * 180.0
				CreateMsg(GetLocalString("msg", "gentlemuscle"))
			EndIf
		Else
			For i = 0 To 1
				If ChannelPlaying(I_427\SoundCHN[i]) Then StopChannel(I_427\SoundCHN[i]) : I_427\SoundCHN[i] = 0
			Next
		EndIf
	Else
		If PrevI427Timer - fps\Factor[0] < 70.0 * 360.0 And I_427\Timer >= 70.0 * 360.0
			CreateMsg(GetLocalString("msg", "muscleswelling"))
		ElseIf PrevI427Timer - fps\Factor[0] < 70.0 * 390.0 And I_427\Timer >= 70.0 * 390.0
			CreateMsg(GetLocalString("msg", "nolegs"))
		EndIf
		I_427\Timer = I_427\Timer + fps\Factor[0]
		If I_427\Sound[0] = 0 Then I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		If I_427\Sound[1] = 0 Then I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
		Next
		If Rnd(200) < 2.0
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(me\Collider) + Rnd(-0.05, 0.05), EntityY(me\Collider) - 0.05, EntityZ(me\Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(DECAL_427, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, Rnd(0.03, 0.08) * 2.0)
			de\SizeChange = Rnd(0.001, 0.0015) : de\MaxSize = de\Size + 0.009
			EntityParent(de\OBJ, PlayerRoom\OBJ)
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 3)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * opt\SFXVolume * opt\MasterVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			FreeEntity(Pvt) : Pvt = 0
			me\BlurTimer = 800.0
		EndIf
		If I_427\Timer >= 70.0 * 420.0
			msg\DeathMsg = GetLocalString("death", "morepower")
			Kill()
		ElseIf I_427\Timer >= 70.0 * 390.0
			If (Not me\Crouch) Then SetCrouch(True)
		EndIf
	EndIf
End Function

Type SCP294
	Field Using%
	Field ToInput$
End Type

Global I_294.SCP294

Function Update294%()
	Local it.Items
	Local x#, y#, xTemp%, yTemp%, StrTemp$, Temp%
	Local Sep1%, Sep2%, Alpha#, Glow%
	Local R%, G%, B%
	
	x = mo\Viewport_Center_X - (ImageWidth(t\ImageID[5]) / 2)
	y = mo\Viewport_Center_Y - (ImageHeight(t\ImageID[5]) / 2)
	
	Temp = (PlayerRoom\SoundCHN = 0)
	
	If Temp
		If mo\MouseHit1
			xTemp = Floor((MousePosX - x - (228 * MenuScale)) / (35.5 * MenuScale))
			yTemp = Floor((MousePosY - y - (342 * MenuScale)) / (36.5 * MenuScale))
			
			Temp = False
			
			If (yTemp >= 0 And yTemp < 5) And (xTemp >= 0 And xTemp < 10)
				PlaySound_Strict(ButtonSFX)
				
				StrTemp = ""
				
				Select yTemp
					Case 0
						;[Block]
						StrTemp = ((xTemp + 1) Mod 10)
						;[End Block]
					Case 1
						;[Block]
						Select xTemp
							Case 0
								;[Block]
								StrTemp = "Q"
								;[End Block]
							Case 1
								;[Block]
								StrTemp = "W"
								;[End Block]
							Case 2
								;[Block]
								StrTemp = "E"
								;[End Block]
							Case 3
								;[Block]
								StrTemp = "R"
								;[End Block]
							Case 4
								;[Block]
								StrTemp = "T"
								;[End Block]
							Case 5
								;[Block]
								StrTemp = "Y"
								;[End Block]
							Case 6
								;[Block]
								StrTemp = "U"
								;[End Block]
							Case 7
								;[Block]
								StrTemp = "I"
								;[End Block]
							Case 8
								;[Block]
								StrTemp = "O"
								;[End Block]
							Case 9
								;[Block]
								StrTemp = "P"
								;[End Block]
						End Select
						;[End Block]
					Case 2
						;[Block]
						Select Int(xTemp)
							Case 0
								;[Block]
								StrTemp = "A"
								;[End Block]
							Case 1
								;[Block]
								StrTemp = "S"
								;[End Block]
							Case 2
								;[Block]
								StrTemp = "D"
								;[End Block]
							Case 3
								;[Block]
								StrTemp = "F"
								;[End Block]
							Case 4
								;[Block]
								StrTemp = "G"
								;[End Block]
							Case 5
								;[Block]
								StrTemp = "H"
								;[End Block]
							Case 6
								;[Block]
								StrTemp = "J"
								;[End Block]
							Case 7
								;[Block]
								StrTemp = "K"
								;[End Block]
							Case 8
								;[Block]
								StrTemp = "L"
								;[End Block]
							Case 9 ; ~ Dispense
								;[Block]
								Temp = True
								;[End Block]
						End Select
					Case 3
						;[Block]
						Select Int(xTemp)
							Case 0
								;[Block]
								StrTemp = "Z"
								;[End Block]
							Case 1
								;[Block]
								StrTemp = "X"
								;[End Block]
							Case 2
								;[Block]
								StrTemp = "C"
								;[End Block]
							Case 3
								;[Block]
								StrTemp = "V"
								;[End Block]
							Case 4
								;[Block]
								StrTemp = "B"
								;[End Block]
							Case 5
								;[Block]
								StrTemp = "N"
								;[End Block]
							Case 6
								;[Block]
								StrTemp = "M"
								;[End Block]
							Case 7
								;[Block]
								StrTemp = "-"
								;[End Block]
							Case 8
								;[Block]
								StrTemp = " "
								;[End Block]
							Case 9
								;[Block]
								I_294\ToInput = Left(I_294\ToInput, Max(Len(I_294\ToInput) - 1, 0.0))
								;[End Block]
						End Select
					Case 4
						;[Block]
						StrTemp = " "
						;[End Block]
				End Select
			EndIf
			
			I_294\ToInput = I_294\ToInput + StrTemp
			
			If Temp And I_294\ToInput <> "" ; ~ Dispense
				I_294\ToInput = Trim(I_294\ToInput)
				If Left(I_294\ToInput, Min(7, Len(I_294\ToInput))) = "cup of "
					I_294\ToInput = Right(I_294\ToInput, Len(I_294\ToInput) - 7)
				ElseIf Left(I_294\ToInput, Min(9, Len(I_294\ToInput))) = "a cup of "
					I_294\ToInput = Right(I_294\ToInput, Len(I_294\ToInput) - 9)
				EndIf
				
				If I_294\ToInput <> ""
					Local Drink$ = FindSCP294Drink(I_294\ToInput, True)
				EndIf
				
				If Drink <> "Null"
					StrTemp = GetFileLocalString(SCP294File, Drink, "Dispense Sound", "", False)
					If StrTemp = ""
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound(StrTemp))
					EndIf
					
					If me\UsedMastercard
						PlaySound_Strict(LoadTempSound("SFX\SCP\294\PullMasterCard.ogg"))
						
						Local i%
						
						If ItemAmount < MaxItemAmount
							For i = 0 To MaxItemAmount - 1
								If Inventory(i) = Null
									Inventory(i) = CreateItem("Mastercard", "mastercard", 1.0, 1.0, 1.0)
									Inventory(i)\Picked = True
									Inventory(i)\Dropped = -1
									Inventory(i)\ItemTemplate\Found = True
									HideEntity(Inventory(i)\Collider)
									EntityType(Inventory(i)\Collider, HIT_ITEM)
									EntityParent(Inventory(i)\Collider, 0)
									Exit
								EndIf
							Next
						Else
							it.Items = CreateItem("Mastercard", "mastercard", EntityX(me\Collider), EntityY(me\Collider) + 0.3, EntityZ(me\Collider))
							it\ItemTemplate\Found = True
							EntityType(it\Collider, HIT_ITEM)
						EndIf
					EndIf
					
					If StringToBoolean(GetFileLocalString(SCP294File, Drink, "Explosion", "", False))
						me\ExplosionTimer = 135.0
						msg\DeathMsg = GetFileLocalString(SCP294File, Drink, "Death Message", "", False)
					EndIf
					
					StrTemp = GetFileLocalString(SCP294File, Drink, "Color", "", False)
					
					Sep1 = Instr(StrTemp, ", ", 1)
					Sep2 = Instr(StrTemp, ", ", Sep1 + 1)
					R = Trim(Left(StrTemp, Sep1 - 1))
					G = Trim(Mid(StrTemp, Sep1 + 1, Sep2 - Sep1 - 1))
					B = Trim(Right(StrTemp, Len(StrTemp) - Sep2))
					
					Alpha = Float(GetFileLocalString(SCP294File, Drink, "Alpha", 1.0, False))
					Glow = StringToBoolean(GetFileLocalString(SCP294File, Drink, "Glow", "", False))
					If Glow Then Alpha = -Alpha
					
					it.Items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1], True), EntityY(PlayerRoom\Objects[1], True), EntityZ(PlayerRoom\Objects[1], True), R, G, B, Alpha)
					it\Name = "Cup of " + Drink
					it\DisplayName = Format(GetLocalString("items", "cupof"), I_294\ToInput)
					EntityType(it\Collider, HIT_ITEM)
				Else
					; ~ Out of range
					I_294\ToInput = GetLocalString("misc", "ofr")
					PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\OutOfRange.ogg"))
				EndIf
			EndIf
		EndIf
		
		If mo\MouseHit2 Lor (Not I_294\Using)
			I_294\Using = False
			I_294\ToInput = ""
			StopMouseMovement()
		EndIf
	Else ; ~ Playing a dispensing sound
		If I_294\ToInput <> GetLocalString("misc", "ofr") Then I_294\ToInput = GetLocalString("misc", "dispensing")
		
		If (Not ChannelPlaying(PlayerRoom\SoundCHN))
			If I_294\ToInput <> GetLocalString("misc", "ofr")
				I_294\Using = False
				me\UsedMastercard = False
				StopMouseMovement()
				
				Local e.Events
				
				For e.Events = Each Events
					If PlayerRoom = e\room
						e\EventState2 = 0.0
						Exit
					EndIf
				Next
			EndIf
			I_294\ToInput = ""
			PlayerRoom\SoundCHN = 0
		EndIf
	EndIf
End Function

Function Render294%()
	Local x#, y#, xTemp%, yTemp%, Temp%
	
	ShowPointer()
	
	x = mo\Viewport_Center_X - (ImageWidth(t\ImageID[5]) / 2)
	y = mo\Viewport_Center_Y - (ImageHeight(t\ImageID[5]) / 2)
	DrawBlock(t\ImageID[5], x, y)
	RenderCursor()
	
	Temp = (PlayerRoom\SoundCHN = 0)
	
	TextEx(x + (905 * MenuScale), y + (185 * MenuScale), Right(I_294\ToInput, 13), True, True)
	
	If Temp
		If mo\MouseHit2 Lor (Not I_294\Using) Then HidePointer()
	Else ; ~ Playing a dispensing sound
		If (Not ChannelPlaying(PlayerRoom\SoundCHN))
			If I_294\ToInput <> GetLocalString("misc", "ofr") Then HidePointer()
		EndIf
	EndIf
End Function

Type SCP1025
	Field State#[8]
End Type

Global I_1025.SCP1025

Function Update1025%()
	Local i%
	Local Factor1025# = fps\Factor[0] * I_1025\State[7]
	
	For i = 0 To 6
		If I_1025\State[i] > 0.0
			Select i
				Case 0 ; ~ Common cold
					;[Block]
					UpdateCough(1000)
					me\Stamina = me\Stamina - (Factor1025 * 0.3)
					;[End Block]
				Case 1 ; ~ Chicken pox
					;[Block]
					If Rand(9000) = 1 Then CreateMsg(GetLocalString("msg", "skinitchy"))
					;[End Block]
				Case 2 ; ~ Cancer of the lungs
					;[Block]
					UpdateCough(800)
					me\Stamina = me\Stamina - (Factor1025 * 0.1)
					;[End Block]
				Case 3 ; ~ Appendicitis
					; ~ 0.035 / sec = 2.1 / min
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then I_1025\State[i] = I_1025\State[i] + (Factor1025 * 0.0005)
					If I_1025\State[i] > 20.0
						If I_1025\State[i] - Factor1025 <= 20.0 Then CreateMsg(GetLocalString("msg", "stomachunbearable"))
						me\Stamina = me\Stamina - (Factor1025 * 0.3)
					ElseIf I_1025\State[i] > 10.0
						If I_1025\State[i] - Factor1025 <= 10.0 Then CreateMsg(GetLocalString("msg", "stomachaching"))
					EndIf
					;[End Block]
				Case 4 ; ~ Asthma
					;[Block]
					If me\Stamina < 35.0
						UpdateCough(Int(140.0 + me\Stamina * 8.0))
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 10.0 + me\Stamina * 15.0)
					EndIf
					;[End Block]
				Case 5 ; ~ Cardiac arrest
					;[Block]
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then I_1025\State[i] = I_1025\State[i] + (Factor1025 * 0.35)
					
					; ~ 35 / sec
					If I_1025\State[i] > 110.0
						me\HeartBeatRate = 0.0
						me\BlurTimer = Max(me\BlurTimer, 500.0)
						If I_1025\State[i] > 140.0
							msg\DeathMsg = GetLocalString("death", "1025")
							Kill()
						EndIf
					Else
						me\HeartBeatRate = Max(me\HeartBeatRate, 70.0 + I_1025\State[i])
						me\HeartBeatVolume = 1.0
					EndIf
					;[End Block]
				Case 6 ; ~ Secondary polycythemia
					;[Block]
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then I_1025\State[i] = I_1025\State[i] + 0.00025 * Factor1025 * (100.0 / I_1025\State[i])
					me\Stamina = Min(100.0, me\Stamina + (90.0 - me\Stamina) * I_1025\State[i] * Factor1025 * 0.00008)
					If I_1025\State[i] > 15.0 And I_1025\State[i] - Factor1025 <= 15.0 Then CreateMsg(GetLocalString("msg", "energetic"))
					;[End Block]
			End Select
		EndIf
	Next
End Function

Type SCP1499
	Field Using%
	Field PrevX#, PrevY#, PrevZ#
	Field PrevRoom.Rooms
	Field x#, y#, z#
	Field Sky%
End Type

Global I_1499.SCP1499

Function UpdateLeave1499%()
	Local r.Rooms, it.Items, r2.Rooms, r1499.Rooms
	Local RID% = PlayerRoom\RoomTemplate\RoomID
	Local i%
	
	If I_1499\Using = 0 And RID = r_dimension_1499
		For r.Rooms = Each Rooms
			If r = I_1499\PrevRoom
				me\BlinkTimer = -1.0
				I_1499\x = EntityX(me\Collider)
				I_1499\y = EntityY(me\Collider)
				I_1499\z = EntityZ(me\Collider)
				TeleportEntity(me\Collider, I_1499\PrevX, I_1499\PrevY + 0.05, I_1499\PrevZ)
				TeleportToRoom(r)
				If RID = r_room3_storage And EntityY(me\Collider) < (-4600.0) * RoomScale
					For i = 0 To 3
						PlayerRoom\NPC[i]\State = 2.0
						PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True), EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True) + 0.2, EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True))
						ResetEntity(PlayerRoom\NPC[i]\Collider)
						PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1.0
						If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState - 3)
					Next
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\RoomID = r_dimension_1499
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\DistTimer = 0.0
					If it\ItemTemplate\TempName = "scp1499" Lor it\ItemTemplate\TempName = "fine1499"
						Local ItemPosY# = EntityY(it\Collider)
						Local RoomPosY# = EntityY(r1499\OBJ)
						
						If ItemPosY >= RoomPosY - 5.0
							PositionEntity(it\Collider, I_1499\PrevX, I_1499\PrevY + (ItemPosY - RoomPosY), I_1499\PrevZ)
							ResetEntity(it\Collider)
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Exit.ogg"))
				I_1499\PrevX = 0.0
				I_1499\PrevY = 0.0
				I_1499\PrevZ = 0.0
				I_1499\PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
End Function

Function CheckForPlayerInFacility%()
	; ~ False (= 0): Player is not in facility (mostly meant for "dimension_1499")
	; ~ True (= 1): Player is in facility
	; ~ 2: Player is in tunnels (maintenance tunnels / SCP-049's tunnels / SCP-939's storage room, etc...)
	Local PlayerPosY# = EntityY(me\Collider)
	
	If PlayerPosY > 100.0 Then Return(0)
	If PlayerPosY < -10.0 Then Return(2)
	If (PlayerPosY > 7.0) And (PlayerPosY <= 100.0) Then Return(2)
	Return(1)
End Function

Function TeleportEntity%(Entity%, x#, y#, z#, CustomRadius# = 0.3, IsGlobal% = False, PickRange# = 2.0, Dir% = False)
	Local Pvt%, Pick#
	; ~ Dir = 0 - towards the floor (default)
	; ~ Dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	Pvt = CreatePivot()
	PositionEntity(Pvt, x, y + 0.05, z, IsGlobal)
	If (Not Dir)
		RotateEntity(Pvt, 90.0, 0.0, 0.0)
	Else
		RotateEntity(Pvt, -90.0, 0.0, 0.0)
	EndIf
	Pick = EntityPick(Pvt, PickRange)
	If Pick <> 0
		If (Not Dir)
			PositionEntity(Entity, x, PickedY() + CustomRadius + 0.02, z, IsGlobal)
		Else
			PositionEntity(Entity, x, PickedY() + CustomRadius - 0.02, z, IsGlobal)
		EndIf
	Else
		PositionEntity(Entity, x, y, z, IsGlobal)
	EndIf
	FreeEntity(Pvt) : Pvt = 0
	ResetEntity(Entity)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS