#!/bin/bash

# Visual styling
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BOLD}🚀 Vehicle Shop Management System Launcher${NC}"
echo "----------------------------------------"

# Function to check command existence
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# 1. Check for Java (JDK 17+)
echo -e "\nChecking for Java..."
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java found (${JAVA_VERSION})${NC}"
else
    echo -e "${RED}❌ Java not found.${NC}"
    echo "Attempting to install OpenJDK 17..."
    
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$NAME
        
        case $ID in
            ubuntu|debian|pop|linuxmint|kali)
                echo "Detected Debian/Ubuntu-based system."
                sudo apt update && sudo apt install -y openjdk-17-jdk
                ;;
            fedora|rhel|centos)
                echo "Detected Fedora/RHEL-based system."
                sudo dnf install -y java-17-openjdk-devel
                ;;
            arch|manjaro|endeavouros)
                echo "Detected Arch-based system."
                sudo pacman -S --noconfirm jdk17-openjdk
                ;;
            opensuse*|suse)
                echo "Detected OpenSUSE."
                sudo zypper install -y java-17-openjdk-devel
                ;;
            *)
                echo -e "${RED}Unsupported distribution for auto-install.${NC}"
                echo "Please install JDK 17 manualy."
                exit 1
                ;;
        esac
        
        # Verify installation
        if command_exists java; then
             echo -e "${GREEN}✓ Java installed successfully!${NC}"
        else
             echo -e "${RED}Failed to install Java. Please install JDK 17 manually.${NC}"
             exit 1
        fi
    else
        echo -e "${RED}Cannot detect OS. Please install JDK 17 manually.${NC}"
        exit 1
    fi
fi

# 2. Check for Gradle wrapper permissions
if [ ! -x "./gradlew" ]; then
    echo -e "\nSetting executable permissions for Gradle wrapper..."
    chmod +x ./gradlew
fi

# 3. Build and Run
echo -e "\n${BOLD}Building and Running Application...${NC}"
./gradlew run
