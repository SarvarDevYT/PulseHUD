@echo off
chcp 65001 >nul
title PulseHUD Version Switcher

echo ========================================
echo     PulseHUD - Versiya almashtirish
echo ========================================
echo.
echo Hozirgi versiya: 1.20.1
echo Mavjud versiyalar:
echo   1) 1.20.1 (Minecraft 1.20 - 1.20.4)
echo   2) 1.21.11 (Minecraft 1.21 - 1.21.11)
echo.
set /p choice="Versiyani tanlang (1 yoki 2): "

if "%choice%"=="1" (
    echo.
    echo 1.20.1 versiyasiga o'tkazilmoqda...
    
    copy /Y "version-1201\gradle.properties" "gradle.properties" >nul
    copy /Y "version-1201\build.gradle" "build.gradle" >nul
    copy /Y "version-1201\pulsehud.mixins.json" "src\main\resources\pulsehud.mixins.json" >nul
    copy /Y "version-1201\HudRenderer.java" "src\main\java\com\pulsehud\hud\HudRenderer.java" >nul
    copy /Y "version-1201\InGameHudMixin.java" "src\main\java\com\pulsehud\mixin\InGameHudMixin.java" >nul
    copy /Y "version-1201\src\main\java\com\pulsehud\PulseHUDClient.java" "src\main\java\com\pulsehud\PulseHUDClient.java" >nul
    copy /Y "version-1201\src\main\java\com\pulsehud\widget\impl\PotionEffectsWidget.java" "src\main\java\com\pulsehud\widget\impl\PotionEffectsWidget.java" >nul
    copy /Y "version-1201\src\main\java\com\pulsehud\widget\impl\SelectedItemWidget.java" "src\main\java\com\pulsehud\widget\impl\SelectedItemWidget.java" >nul
    copy /Y "version-1201\src\main\java\com\pulsehud\mixin\WorldRendererMixin.java" "src\main\java\com\pulsehud\mixin\WorldRendererMixin.java" >nul
    copy /Y "version-1201\src\main\java\com\pulsehud\mixin\EntityRenderDispatcherMixin.java" "src\main\java\com\pulsehud\mixin\EntityRenderDispatcherMixin.java" >nul
    
    if exist "src\main\java\com\pulsehud\mixin\ExperienceBarMixin.java" (
        del "src\main\java\com\pulsehud\mixin\ExperienceBarMixin.java"
    )
    
    echo.
    echo ✅ 1.20.1 versiyasiga o'tkazildi!
    echo Build qilish uchun: gradlew build
    goto end
)

if "%choice%"=="2" (
    echo.
    echo 1.21.11 versiyasiga o'tkazilmoqda...
    
    copy /Y "version-1211\gradle.properties" "gradle.properties" >nul
    copy /Y "version-1211\build.gradle" "build.gradle" >nul
    copy /Y "version-1211\pulsehud.mixins.json" "src\main\resources\pulsehud.mixins.json" >nul
    copy /Y "version-1211\HudRenderer.java" "src\main\java\com\pulsehud\hud\HudRenderer.java" >nul
    copy /Y "version-1211\InGameHudMixin.java" "src\main\java\com\pulsehud\mixin\InGameHudMixin.java" >nul
    copy /Y "version-1211\ExperienceBarMixin.java" "src\main\java\com\pulsehud\mixin\ExperienceBarMixin.java" >nul
    copy /Y "version-1211\src\main\java\com\pulsehud\PulseHUDClient.java" "src\main\java\com\pulsehud\PulseHUDClient.java" >nul
    copy /Y "version-1211\src\main\java\com\pulsehud\widget\impl\PotionEffectsWidget.java" "src\main\java\com\pulsehud\widget\impl\PotionEffectsWidget.java" >nul
    copy /Y "version-1211\src\main\java\com\pulsehud\widget\impl\SelectedItemWidget.java" "src\main\java\com\pulsehud\widget\impl\SelectedItemWidget.java" >nul
    if exist "src\main\java\com\pulsehud\mixin\EntityRenderDispatcherMixin.java" del "src\main\java\com\pulsehud\mixin\EntityRenderDispatcherMixin.java"
    if exist "src\main\java\com\pulsehud\mixin\WorldRendererMixin.java" del "src\main\java\com\pulsehud\mixin\WorldRendererMixin.java"
    
    echo.
    echo ✅ 1.21.11 versiyasiga o'tkazildi!
    echo Build qilish uchun: gradlew build
    goto end
)

echo.
echo ❌ Notog'ri tanlov! Iltimos 1 yoki 2 ni tanlang.
:end
echo.
pause
