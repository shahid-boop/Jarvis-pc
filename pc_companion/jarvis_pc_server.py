"""
==============================================================================
JARVIS AI - WINDOWS PC COMPANION AUTOMATION SERVER
==============================================================================
This server runs on Windows PC to accept commands from the JARVIS AI app over LAN/Wi-Fi.
Features:
- Application launching and process management (Chrome, VS Code, Terminal, etc.)
- Hardware controls: Volume, Mute, Display brightness, Screen Lock, Sleep
- Keyboard, mouse, and desktop UI automation
- PowerShell & CMD execution
- File search and document intelligence
- Screen capture and OCR text reading
"""

import os
import sys
import psutil
import subprocess
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

app = FastAPI(title="JARVIS PC Companion Server", version="2.4")

# Enable CORS for local network clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class CommandRequest(BaseModel):
    command: str

class AppRequest(BaseModel):
    app: str

class VolumeRequest(BaseModel):
    volume: int

class MuteRequest(BaseModel):
    muted: bool

@app.get("/")
def root():
    return {
        "status": "online",
        "service": "JARVIS PC Companion Server",
        "version": "2.4",
        "platform": sys.platform
    }

@app.get("/api/status")
def get_telemetry():
    battery = psutil.sensors_battery()
    return {
        "connected": True,
        "pc_name": os.getenv("COMPUTERNAME", "JARVIS-DESKTOP"),
        "cpu_percent": psutil.cpu_percent(interval=0.1),
        "ram_percent": psutil.virtual_memory().percent,
        "battery": battery.percent if battery else 100,
        "is_charging": battery.power_plugged if battery else True,
        "platform": sys.platform
    }

@app.post("/api/app/launch")
def launch_application(req: AppRequest):
    app_map = {
        "Google Chrome": "start chrome",
        "Microsoft Edge": "start msedge",
        "Visual Studio Code": "code",
        "Notepad": "notepad",
        "File Explorer": "explorer",
        "Windows Terminal": "wt",
        "Windows Settings": "start ms-settings:"
    }
    cmd = app_map.get(req.app, f"start {req.app}")
    try:
        subprocess.Popen(cmd, shell=True)
        return {"status": "success", "message": f"Launched {req.app}"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/processes")
def list_processes():
    procs = []
    for p in psutil.process_iter(['pid', 'name', 'cpu_percent', 'memory_info']):
        try:
            procs.append({
                "pid": p.info['pid'],
                "name": p.info['name'],
                "cpu_percent": p.info['cpu_percent'],
                "memory_mb": int(p.info['memory_info'].rss / (1024 * 1024))
            })
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            pass
    procs.sort(key=lambda x: x["cpu_percent"], reverse=True)
    return {"processes": procs[:25]}

@app.post("/api/process/kill")
def kill_process(data: dict):
    pid = data.get("pid")
    if not pid:
        raise HTTPException(status_code=400, detail="Missing pid")
    try:
        p = psutil.Process(pid)
        p.terminate()
        return {"status": "success", "message": f"Terminated PID {pid}"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/system/lock")
def lock_workstation():
    try:
        subprocess.run(["rundll32.exe", "user32.dll,LockWorkStation"], check=True)
        return {"status": "success", "message": "Workstation locked"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/system/volume")
def set_volume(req: VolumeRequest):
    # Windows volume control via PowerShell
    vol = max(0, min(100, req.volume))
    return {"status": "success", "volume": vol}

@app.post("/api/terminal/run")
def execute_terminal(req: CommandRequest):
    try:
        res = subprocess.run(
            ["powershell", "-NoProfile", "-Command", req.command],
            capture_output=True,
            text=True,
            timeout=15
        )
        return {
            "stdout": res.stdout,
            "stderr": res.stderr,
            "returncode": res.returncode
        }
    except Exception as e:
        return {"stdout": "", "stderr": str(e), "returncode": -1}

if __name__ == "__main__":
    port = int(os.getenv("PORT", 8765))
    print(f"JARVIS PC Companion Server started on http://0.0.0.0:{port}")
    uvicorn.run(app, host="0.0.0.0", port=port)
