CHESS GAME - REFACTORED STRUCTURE
Package: com.example.chessgame

Copy all .java files in:
  com/example/chessgame/
into:
  app/src/main/java/com/example/chessgame/

FILES
1. MainActivity.java
   - Android Activity lifecycle and coordination
   - Receives user actions and coordinates Chess + UI

2. ChessScreen.java
   - Builds the Android UI programmatically
   - Owns no chess rules

3. ChessBoardRenderer.java
   - Draws pieces, board backgrounds and checked-king highlight
   - Owns no chess rules

4. Chess.java
   - Public facade / game lifecycle
   - Keeps the original public API used by MainActivity

5. ChessRules.java
   - Legal move filtering, check, checkmate/stalemate support

6. ChessHistory.java
   - Fivefold repetition state and one-level undo snapshot

7. Piece.java
   - Small facade preserving Piece constructors and possibleMoves()

8. PieceMoveGenerator.java
   - Dispatches a piece type to the correct move generator

9. PawnMoveGenerator.java
   - Pawn movement and original en-passant move code generation

10. KnightMoveGenerator.java
   - Knight movement

11. SlidingMoveGenerator.java
   - Rook/Bishop/Queen sliding movement

12. KingMoveGenerator.java
   - King movement and original castling move-code generation

IMPORTANT
- Package name remains com.example.chessgame.
- Resource names (R.drawable.*) are unchanged.
- Original move codes are unchanged: 2 normal, 3 capture, 4 king-side castle,
  5 queen-side castle, 7 en passant candidate.
- Promotion values and piece numbering are unchanged.
- Chess public methods used by MainActivity are preserved.
- This refactor is intended as a structural split, not a chess-rule rewrite.

VALIDATION PERFORMED
- Core Java classes compile with javac.
- A deterministic 50-step behavior trace was run against the original Chess/Piece
  and the refactored core. The traces matched.
- Android UI classes require your Android project/SDK/resources to compile, because
  they reference AppCompatActivity and existing R.drawable resources.

BACKUP
Original uploaded files are kept in backup_original/ for comparison only.
Do NOT copy backup_original into app/src/main/java.
