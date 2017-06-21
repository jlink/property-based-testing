module ScoreboardApp where

import System.IO
import Data.Maybe
import Data.Char
import Data.List

import Scoreboard

data Command = Exit | ResetBoard | SelectA | SelectB | Increment | Decrement
  deriving (Show, Eq)

run :: IO()
run = do
  putStrLn $ "SCOREBOARD started."
  loop getContents putStrLn
  putStrLn $ "SCOREBOARD stopped."
  return ()

loop :: IO String -> (String -> IO ()) -> IO ()
loop contentsReader printer = do
  contents <- contentsReader
  let commandLines = lines contents
  let messages = process newScoreboard commandLines
  mapM_ printer messages

process :: Scoreboard -> [String] -> [String]
process scoreboard lines = initialMessage : messages where
  initialMessage = formatCurrentScore scoreboard
  messages = processCommands scoreboard $ toCommands lines

toCommands :: [String] -> [Command]
toCommands lines = catMaybes $ map (toCommand . sanitize) lines

sanitize :: String -> String
sanitize line = trim $ map toLower line

trim :: String -> String
trim = dropWhileEnd isSpace . dropWhile isSpace

toCommand :: String -> Maybe Command
toCommand "x" = Just Exit
toCommand "r" = Just ResetBoard
toCommand "a" = Just SelectA
toCommand "b" = Just SelectB
toCommand "+" = Just Increment
toCommand "-" = Just Decrement
toCommand _ = Nothing

processCommands :: Scoreboard -> [Command] -> [String]
processCommands scoreboard [] = []
processCommands scoreboard (Exit : _) = []
processCommands scoreboard (key : rest) =
  (message action scoreboard nextScoreboard : processCommands nextScoreboard rest) where
    action = getAction key
    nextScoreboard = operation action scoreboard

type Operation = Scoreboard -> Scoreboard
type Message = Scoreboard -> Scoreboard -> String
data Action = Action { operation :: Operation, message :: Message }

getAction :: Command -> Action
getAction ResetBoard = Action {
  operation = \scoreboard -> newScoreboard,
  message = \oldScorebaord newScoreboard -> formatCurrentScore  newScoreboard
}
getAction SelectA = createSelectAction TeamA
getAction SelectB = createSelectAction TeamB
getAction Increment = createScoringAction incrementScore
getAction Decrement = createScoringAction decrementScore

createSelectAction selection = Action {
  operation = \scoreboard -> selectTeam scoreboard selection,
  message = \oldScorebaord newScoreboard -> formatSelection $ currentSelection newScoreboard
}

createScoringAction scoringFunction = Action {
  operation = \scoreboard -> case scoreboard of
      Scoreboard _ None -> scoreboard
      _                 -> scoringFunction scoreboard,
  message = \oldScorebaord newScoreboard -> formatCurrentScore newScoreboard
}

formatCurrentScore :: Scoreboard -> String
formatCurrentScore scoreboard = formatScore $ currentScore scoreboard


formatScore :: Score -> String
formatScore (a, b) = (formatTeamScore a) ++ ":" ++ (formatTeamScore b)

formatTeamScore :: Int -> String
formatTeamScore s = leftPad0 3 (show s)

leftPad0 :: Int -> String -> String
leftPad0 n s
    | length s < n  = replicate (n - length s) '0' ++ s
    | otherwise = s

formatSelection :: Selection -> String
formatSelection TeamA = "Team A selected"
formatSelection TeamB = "Team B selected"
