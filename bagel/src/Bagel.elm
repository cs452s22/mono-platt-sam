module Bagel exposing (main)

-- For Api.Object.Show scope
import Api.Object exposing (Show)
import Api.Object.Show as ShowFields
import Api.Query as Query
import Browser
import Graphql.Http
import Graphql.Operation exposing (RootQuery)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, pre, text)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import RemoteData exposing (RemoteData)

-- Main

main = 
    Browser.sandbox { init = init, update = update, view = view }

-- Model

type alias Model =
    { content : String
    , inputContent : String
    }

init : Model
init =
    { content = ""
    , inputContent = ""
    }

-- Update

type Msg
    = Scan
    | Parse
    | Change String

update : Msg -> Model -> Model
update msg model =
    case msg of
        Change newInputContent ->
            { model | inputContent = newInputContent }
        Scan ->
            { model | content = String.join " " (List.map (\str -> "<" ++ str ++ ">") (String.words model.inputContent)) }
        Parse ->
            { model | content = String.join "\n" (List.map (\str -> "<" ++ str ++ ">") (String.words model.inputContent)) }

view : Model -> Html Msg
view model = 
    div []
    [ input [ placeholder "Your text here", onInput Change ] []
    , button [ onClick Scan ] [ text "Scan" ]
    , button [ onClick Parse ] [ text "Parse" ]
    , pre [] [ text ( model.content ) ]
    ]