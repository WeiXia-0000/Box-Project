# BoxShogi - Box Product-Themed Japanese Shogi Variant

## Project Overview

BoxShogi is a 5x5 board game based on Japanese Shogi, where pieces represent various Box company products. This is a complete Java implementation supporting both interactive gameplay and file-based testing modes.

## Features

### 🎮 Game Modes
- **Interactive Mode**: Two players compete through keyboard input
- **File Mode**: Reads game state and move instructions from files for automated testing

### 🏁 Game Rules Implementation
- **Complete Movement Rules**: All 6 piece types with their movement logic
- **Promotion System**: Piece ability enhancement when entering promotion zones
- **Capture System**: Captured pieces can be redeployed on the board
- **Check Detection**: Automatic detection of check states and available moves
- **Win Conditions**: Multiple end-game scenarios including checkmate, illegal moves, and draws

### 🧩 Core Components

#### 1. Game Engine (`BoxShogi.java`)
- Manages game state and turns
- Handles user input and move validation
- Controls game flow and win condition determination

#### 2. Board Management (`Board.java`)
- 5x5 board representation
- Piece placement and removal
- Board state visualization

#### 3. Piece System (`Piece.java`)
- Movement rules for all 6 different piece types
- Promotion state management
- Move validation logic

#### 4. Player Management (`Player.java`)
- Player state tracking
- Captured piece management
- Piece position recording

#### 5. Utility Class (`Utils.java`)
- Test case parsing
- File input processing

## Game Rules

### Board and Pieces

The game is played on a 5x5 board with 6 piece types per player:

| Piece | Symbol | Movement Rule | Promoted Ability |
|-------|--------|---------------|------------------|
| Box Drive (King) | d/D | One square in any direction | Cannot be promoted |
| Box Notes (Rook) | n/N | Any number of squares along rows/columns | Can move 1 square or orthogonally |
| Box Governance (Bishop) | g/G | Any number of squares diagonally | Can move 1 square or diagonally |
| Box Shield (Gold General) | s/S | One square in any direction except backward diagonals | Cannot be promoted |
| Box Relay (Silver General) | r/R | One square in any direction except sideways or backward | Moves like Gold General |
| Box Preview (Pawn) | p/P | One square forward | Moves like Gold General |

### Special Rules

#### Promotion Zones
- **lower player**: Row 5 (top row)
- **UPPER player**: Row 1 (bottom row)

#### Capturing and Dropping
- Captured pieces can be redeployed on empty squares
- Dropped pieces are always unpromoted
- Box Preview cannot be dropped in promotion zones or positions causing immediate checkmate

#### Check and Win Conditions
- Player is in check when their Drive is threatened
- Must move to escape check
- Checkmate occurs when unable to escape check
- Illegal moves result in immediate loss

## How to Run

### Compilation
```bash
javac boxshogi/Main.java
```

### Interactive Mode
```bash
java Main -i
```

### File Mode
```bash
java Main -f <test_file_path>
```

### Test Execution
```bash
# macOS/Linux
./test_runners/test-runner-mac

# Windows
cmd /K ./test_runners/test-runner-windows.exe
```

## Input Format

### Move Commands
- `move <start_position> <target_position> [promote]`
  - Example: `move a2 a3` or `move a4 a5 promote`

### Drop Commands
- `drop <piece_type> <position>`
  - Example: `drop s c3` or `drop g a1`

### Position Notation
- Use letters a-e for columns (left to right)
- Use numbers 1-5 for rows (bottom to top)
- Example: `a1` represents bottom-left corner, `e5` represents top-right corner

## Output Format

The game displays:
1. Current board state (5x5 grid)
2. Captured pieces for both players
3. Current player prompt
4. Check status and available moves (if applicable)

Example output:
```
5 | N| G| R| S| D|
4 |__|__|__|__| P|
3 |__|__|__|__|__|
2 | p|__|__|__|__|
1 | d| s| r| g| n|
    a  b  c  d  e

Captures UPPER: 
Captures lower: 

lower> 
```

## Technical Implementation

### Architecture Design
- **Object-Oriented Design**: Clear class separation and responsibility division
- **State Management**: Complete game state tracking
- **Input Validation**: Strict move legality checking
- **Error Handling**: Graceful exception handling and user feedback

### Key Algorithms
- **Move Validation**: Rule-based checking for each piece type
- **Check Detection**: Recursive search for threat paths
- **Path Blocking**: Checking for obstacles in movement paths
- **Win Condition Determination**: Comprehensive evaluation of multiple end-game scenarios

### Performance Optimization
- **Efficient Data Structures**: Using HashMap and LinkedList
- **Minimal Computation**: Complex calculations only when necessary
- **Memory Management**: Proper object lifecycle management

## Test Coverage

The project includes comprehensive test cases covering:
- Basic movement rules
- Promotion mechanisms
- Capturing and dropping
- Check detection
- Illegal move handling
- Edge case scenarios

All test cases are validated through an automated test runner, ensuring code correctness and stability.

## Development Environment

- **Language**: Java 11
- **Platform**: Cross-platform (Windows, macOS, Linux)
- **Build**: Standard Java compiler
- **Testing**: Custom test runner

This implementation demonstrates complete game logic, user interface design, and software engineering best practices, resulting in a fully functional, high-quality BoxShogi game implementation.
