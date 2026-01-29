#!/bin/bash

# Android Project Validation Script
# This script validates that all required files are present

echo "=== EmergencyMesh Android Project Validation ==="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Counter
PASS=0
FAIL=0

check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $1 - MISSING"
        ((FAIL++))
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $1/ - MISSING"
        ((FAIL++))
    fi
}

echo "## Build System Files"
check_file "build.gradle.kts"
check_file "settings.gradle.kts"
check_file "gradle.properties"
check_file "gradlew"
check_file "gradle/wrapper/gradle-wrapper.properties"
check_file "gradle/wrapper/gradle-wrapper.jar"
check_file "app/build.gradle.kts"
check_file "app/proguard-rules.pro"

echo ""
echo "## Android Manifest and Resources"
check_file "app/src/main/AndroidManifest.xml"
check_file "app/src/main/res/values/strings.xml"
check_file "app/src/main/res/values/themes.xml"
check_file "app/src/main/res/values/colors.xml"

echo ""
echo "## Main Activity"
check_file "app/src/main/java/com/emergencymesh/app/MainActivity.kt"

echo ""
echo "## Database - Entities"
check_file "app/src/main/java/com/emergencymesh/app/data/entity/Peer.kt"
check_file "app/src/main/java/com/emergencymesh/app/data/entity/Message.kt"
check_file "app/src/main/java/com/emergencymesh/app/data/entity/SeenMessageId.kt"

echo ""
echo "## Database - DAOs"
check_file "app/src/main/java/com/emergencymesh/app/data/dao/PeerDao.kt"
check_file "app/src/main/java/com/emergencymesh/app/data/dao/MessageDao.kt"
check_file "app/src/main/java/com/emergencymesh/app/data/dao/SeenMessageIdDao.kt"

echo ""
echo "## Database - Core"
check_file "app/src/main/java/com/emergencymesh/app/data/database/AppDatabase.kt"
check_file "app/src/main/java/com/emergencymesh/app/data/database/Converters.kt"

echo ""
echo "## ViewModels"
check_file "app/src/main/java/com/emergencymesh/app/viewmodel/OnboardingViewModel.kt"
check_file "app/src/main/java/com/emergencymesh/app/viewmodel/HomeViewModel.kt"
check_file "app/src/main/java/com/emergencymesh/app/viewmodel/SettingsViewModel.kt"

echo ""
echo "## UI Screens"
check_file "app/src/main/java/com/emergencymesh/app/ui/screens/OnboardingScreen.kt"
check_file "app/src/main/java/com/emergencymesh/app/ui/screens/HomeScreen.kt"
check_file "app/src/main/java/com/emergencymesh/app/ui/screens/SettingsScreen.kt"

echo ""
echo "## UI Theme"
check_file "app/src/main/java/com/emergencymesh/app/ui/theme/Color.kt"
check_file "app/src/main/java/com/emergencymesh/app/ui/theme/Theme.kt"
check_file "app/src/main/java/com/emergencymesh/app/ui/theme/Type.kt"

echo ""
echo "## Documentation"
check_file "README.md"

echo ""
echo "=== Validation Summary ==="
echo -e "${GREEN}Passed:${NC} $PASS"
echo -e "${RED}Failed:${NC} $FAIL"

if [ $FAIL -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ All required files are present!${NC}"
    echo ""
    echo "Project structure is valid and ready to build."
    echo "Note: This project requires Android SDK to build."
    echo "Install Android Studio and open this project to build."
    exit 0
else
    echo ""
    echo -e "${RED}✗ Some required files are missing!${NC}"
    exit 1
fi
