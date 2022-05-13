module Bagel exposing (..)

-- For Api.Object.Token scope
import Api.Enum.TokenType exposing (..)
import Api.Object exposing (Token)
import Api.Object.Token as TokenFields
import Api.Query as Query
import Browser
import Graphql.Http exposing (..)
import Graphql.Operation exposing (RootQuery)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, br, button, div, input, label, p, text, textarea)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import RemoteData exposing (RemoteData)

-- Main

type alias Response =
    String

type alias Token =
    { type_: TokenType
    , lexeme: String
    , line: Int
    }

type Msg
    = GotResponse (RemoteData (Graphql.Http.Error String) String)
    | ChangeCodeText String
    | ChangeInputText String
    | Run -- added for lab 5
    | Sign -- added for lab 5

type alias Model =
    { code : String -- changed from filter to code for lab 3
    , input : String -- added for lab 5
    , tokens : RemoteData (Graphql.Http.Error String) String
    }

query : Model -> SelectionSet String RootQuery
query model =
    Query.run { code = model.code, input = model.input } -- changed from filter to code for lab 3; changed to Query.run for lab 4
    -- Query.sign { code = model.code } -- added for lab 5 part 1

makeRequest : Model -> Cmd Msg
makeRequest model =
    query model
        |> Graphql.Http.queryRequest "http://localhost:8080/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)

type alias Flags =
    ()

init : Flags -> ( Model, Cmd Msg )
init _ =
    ( { tokens = RemoteData.NotAsked, code = "", input = "" }, Cmd.none ) -- changed from filter to code for lab 3

update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GotResponse response ->
            ( { model | tokens = response }, Cmd.none )
        ChangeCodeText s ->
            ( { model | code = s }, Cmd.none ) -- changed from filter to code for lab 3
        ChangeInputText s ->
            ( { model | input = s }, Cmd.none ) -- added during lab 5 part 2
        Run -> -- changed from Scan to Run for lab 4, changed from Run to Sign for lab 5
            ( model, makeRequest model )
        Sign -> -- changed from Scan to Run for lab 4, changed from Run to Sign for lab 5
            ( model, makeRequest model )

main =
    Browser.element
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }

subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none


view : Model -> Html Msg
view model =
    div []
        [ p [] [ text "Usage: Be sure to have parentheses following function calls ie: getC()"]
        , label [] [ text "Code: "]
        , textarea [ value model.code, onInput ChangeCodeText ] [] -- changed from filter to code for lab 3
        , label [] [ text " Input: " ]
        , textarea [ value model.input, onInput ChangeInputText ] []
        , div []
            [ button [ onClick Sign ] [ text "Run Interpretor" ] -- changed from Scan to Run for lab 4, changed from Run to Sign for lab 5
            ]
        , div []
            [ viewResponse model.tokens ]
        ]

-- This function is what adds the brackets and also displays the type
addBrackets : Token -> String
addBrackets model =
    "<" ++ Api.Enum.TokenType.toString(model.type_) ++ ">"


viewResponse model =
    case model of
        RemoteData.NotAsked ->
            -- when the user hasn't run the program
            text ""

        RemoteData.Loading ->
            -- when the program is loading
            text "loading"

        RemoteData.Success response ->
            -- when the program ahs run and the result is available
            -- text (String.join " " (List.map addBrackets response)) -- changed this line to make it work
            text (response)
        RemoteData.Failure err ->
            case err of
                HttpError NetworkError ->
                    -- Cannot connect to server
                    text ("Http Error: Network Error")
                GraphqlError _ errors ->
                    -- Program returns an exception
                    text ("Graphql Error: " ++ Debug.toString errors)
                _ ->
                    -- Other, unknown error
                    text ("Error: " ++ Debug.toString err)