import chromadb

client = chromadb.HttpClient(host="localhost", port=8000)
client.create_collection("spring-ai-docs")
