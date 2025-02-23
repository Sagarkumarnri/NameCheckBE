from fastapi import FastAPI
from sentence_transformers import SentenceTransformer
from pydantic import BaseModel
import uvicorn

# Load model
model = SentenceTransformer('all-MiniLM-L6-v2')

app = FastAPI()

class TextRequest(BaseModel):
    sentences: list

@app.post("/embed")
def get_embeddings(request: TextRequest):
    embeddings = model.encode(request.sentences).tolist()
    return {"embeddings": embeddings}

# Run: python embed_api.py
if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
