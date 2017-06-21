module IoSample
    ( getNameAndGreet, someIo, someFunc
    ) where

import           Data.Char (toUpper)
import           System.IO

someFunc :: IO ()
someFunc = putStrLn "someFunc"

someIo :: IO ()
someIo = do
   getNameAndGreet getName putStrLn

getNameAndGreet :: IO String -> (String -> IO ()) -> IO ()
getNameAndGreet input output = do
  inpStr <- input
  output $ "Welcome to Haskell, " ++ inpStr ++ "!"

-- getName :: IO String
getName = do
  putStrLn "What is your name?"
  getLine
